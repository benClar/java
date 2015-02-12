import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;

/* Mark a Java assignment using "java Mark script [folders]".  If folders are
given, marking is done in each, otherwise marking is done in the current
folder.  It is assumed that programs to be marked are at the top level (not in
daylate or weeklate subfolders).  The script file holds the information about
the tests to perform, and acts as a skeleton for the file feedback.html which
is generated containing the test results.  If folders are specified, a summary
file marks.csv is created.  The active entries in the script file are
surrounded by div tags to make the script file readable as an html page:

<div>ass CW1</div>
   Must come at the beginning; sets up the assessment name for marks.csv

<div>require file</div>
   Checks that the file exists.  It is a case sensitive check, in case a
   program came from a case insensitive file system (Windows, sometimes MacOS).

<div>out2pe text</div>
   Set the expected output for each test, until further notice.  The keyword is
   'out' optionally followed by any of three flags.  If the flag 2 is included,
   an alternative output is specified.  If the flag p is included, the text is
   a pattern, using Java's regular expressions.  If the flag e is included,
   the program is expected to give an error message, i.e. exit with a
   non-zero value, and with the output not including the word "Exception".

<div>mark 3</div>
   Set the number of marks per test, until further notice
   Marks can be rational, e.g. 3/7 for seven tests worth three marks

<div>mark2 1/2</div>
   Set a different mark for a test result which gives the alternative output

<div>javac Program.java</div>
   Compile a program.

<div>java Program args
input line 1
input line 2</div>
   Run a compiled program, with given input lines, or with input from a
   previously set up input.txt file, and check output

<div>include file</div>
   If part of the assignment is hand-marked, this will load and include a
   hand-written file with a mark on the first line, followed by comments

<div>max</div>
   Calculate and report the max mark, to confirm what the assessment is out of
   This is reported just once on the standard output, not in the feedback file

<div>total</div>
   Calculate and report the total mark in the marks.csv file
*/

class Mark
{
    int timeoutSeconds = 10;
    boolean createSummary;
    long markPerTest, mark2PerTest, total, max;
    List<String> script;
    String expected, expected2;
    boolean isPattern, isPattern2;
    boolean isError, isError2;
    PrintWriter out, marks;
    boolean maxPrinted, headerPrinted;

    public static void main(String[] args) throws Exception
    {
        Mark program = new Mark();
        if (args.length == 0)
        {
            System.err.println("Use: java Mark script [folders]");
            System.err.println("If no folders are given, the current");
            System.err.println("folder is marked.");
            System.exit(1);
        }
        if (args.length == 1)
        {
            program.createSummary = false;
            program.run(args[0], new String[] {"."});
        }
        else
        {
            program.createSummary = true;
            String[] folders = new String[args.length-1];
            for (int i=0; i<folders.length; i++) folders[i] = args[i+1];
            program.run(args[0], folders);
        }
    }

    // Loop through the folders.
    void run(String scriptFile, String[] folders) throws Exception
    {
        script = readScript(scriptFile);
        if (createSummary) marks = new PrintWriter(new File("marks.csv"));
        maxPrinted = headerPrinted = false;
        for (String folder : folders)
        {
            File dir = new File(folder);
            if (! dir.isDirectory()) continue;
            mark(folder);
        }
        if (createSummary) marks.close();
    }

    // Read in the script, combining multi-line tests.
    List<String> readScript(String filename) throws Exception
    {
        List<String> lines = readLines(new File(filename));
        for (int i=0; i<lines.size(); i++)
        {
            String line = lines.get(i);
            if (line.indexOf("<div>") < 0) continue;
            while (line.indexOf("</div>") < 0)
            {
                line = line + "\n" + lines.get(i+1);
                lines.remove(i+1);
            }
            lines.set(i, line);
        }
        return lines;
    }

    // Carry out the marking for one folder.
    void mark(String folder) throws Exception
    {
        markPerTest = rat("1");
        total = rat("0");
        max = rat("0");
        out = new PrintWriter(new File(folder + "/feedback.html"));
        for (String line : script)
        {
            if (line.startsWith("<div>"))
            {
                String action = line.substring(5, line.length()-6);
                perform(folder, action);
            }
            else out.println(line);
        }
        out.close();
    }

    // Carry out an action.
    void perform(String folder, String action) throws Exception
    {
        String[] parts = action.split(" +", 2);
        String act = parts[0];
        String text = parts.length < 2 ? "" : parts[1];
        if (act.equals("ass")) doAss(text);
        else if (act.equals("require")) doRequire(folder, text);
        else if (act.equals("mark")) doMark(text);
        else if (act.equals("mark2")) doMark2(text);
        else if (act.startsWith("out")) doOut(act, text);
        else if (act.equals("include")) doInclude(folder, text);
        else if (act.equals("javac")) doJavac(folder, text);
        else if (act.equals("java")) doJava(folder, text);
        else if (act.equals("max")) doMax(text);
        else if (act.equals("total")) doTotal(folder, text);
        else System.err.println("Unknown command " + act);
    }

    // Set the name of the assessment.
    void doAss(String text)
    {
        if (headerPrinted || ! createSummary) return;
        marks.println("Student, " + text);
        headerPrinted = true;
    }

    // Check that a file exists, in a case sensitive way.
    void doRequire(String folder, String text)
    {
        max = plus(max, markPerTest);
        File dir = new File(folder);
        String[] names = dir.list();
        boolean ok = false;
        for (String name: names)
        {
            if (name.equals(text)) ok = true;
        }
        if (ok) pass("require " + text, markPerTest);
        else fail("require " + text, "",
            "The file doesn't exist or its name has the wrong case.\n");
    }

    // Set the mark per test for subsequent tests
    void doMark(String text)
    {
        markPerTest = rat(text);
        mark2PerTest = rat(text);
    }

    // Set the secondary mark per test for subsequent tests
    void doMark2(String text)
    {
        mark2PerTest = rat(text);
    }

    // Set the expected output for subsequent tests
    void doOut(String act, String text)
    {
        boolean two = act.contains("2");
        boolean pat = act.contains("p");
        boolean err = act.contains("e");
        expected2 = null;
        if (two) expected2 = text; else expected = text;
        if (two) isPattern2 = pat; else isPattern = pat;
        if (two) isError2 = err; else isError = err;
    }

    // Include a hand-written part, first line is mark, rest comments.
    void doInclude(String folder, String filename) throws Exception
    {
        File file = new File(folder, filename);
        if (! file.exists())
        {
            out.println("<p>" + folder + ": hand marking not done yet.</p>");
            return;
        }
        List<String> lines = readLines(file);
        int mark = Integer.parseInt(lines.get(0));
        lines.remove(0);
        total = plus(total, rat(mark));
        max = plus(max, markPerTest);
        out.print("<p>Mark: ");
        out.print(mark);
        out.print(" out of ");
        out.print(toString(markPerTest));
        out.println(".</p>");
        out.print("<p>");
        for (String line : lines) out.println(verbatim(line));
        out.println("</p>");
    }

    // Compile a java program
    void doJavac(String folder, String text) throws Exception
    {
        max = plus(max, markPerTest);
        String[] command = ("javac " + text).split(" +");
        String target = command[1];
        target = target.substring(0, target.length()-5);
        target += ".class";
        File dir = new File(folder);
        File classFile = new File(dir, target);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(dir);
        pb.redirectErrorStream(true);
        pb.redirectOutput(
            ProcessBuilder.Redirect.to(new File(dir, "javac.txt")));
        File javacOutFile = new File(folder, "javac.txt");
        Process p = pb.start();
        p.waitFor();
        String javacOut = readString(javacOutFile);
        javacOutFile.delete();
        boolean ok = classFile.exists();
        if (ok) pass("javac " + text, markPerTest);
        else fail("javac " + text, "", javacOut);
    }

    // Run a Java program with specific args as a test.
    void doJava(String folder, String text) throws Exception
    {
        max = plus(max, markPerTest);
        File dir = new File(folder);
        String[] lines = ("java " + text).split("\n");
        String[] command = lines[0].split(" +");
        File input = new File(dir, "input.txt");
        if (lines.length > 1)
        {
            PrintWriter pw = new PrintWriter(input);
            for (int i=1; i<lines.length; i++) pw.println(lines[i]);
            pw.close();
        }
        File program = new File(dir, command[1] + ".class");
        if (! program.exists())
        {
            fail(text, "", "Program not found\n");
            return;
        }
        File output = new File(dir, "outputtxt");
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(dir);
        pb.redirectErrorStream(true);
        if (input.exists())
            pb.redirectInput(ProcessBuilder.Redirect.from(input));
        pb.redirectOutput(ProcessBuilder.Redirect.to(output));
        int exit = runProcess(pb);
        if (exit < 0)
        {
            fail(text, "", "Program timed out; possible infinite loop\n");
            return;
        }
        String actual = readString(output);
        output.delete();
        interpret(text, actual, exit);
    }

    // Interpret the results of a program run.
    void interpret(String text, String actual, int exit)
    {
        if (! isError && exit > 0 || actual.contains("Exception"))
        {
            failExit(text, actual, true);
            return;
        }
        if (isError && (exit == 0))
        {
            failExit(text, actual, false);
            return;
        }
        boolean success;
        if (isPattern) success = actual.matches(expected);
        else success = actual.equals(expected);
        if (success) { pass(text, markPerTest); return; }
        if (expected2 == null) { fail(text, expected, actual); return; }
        if (isPattern2) success = actual.matches(expected2);
        else success = actual.equals(expected2);
        if (success) pass(text, mark2PerTest);
        else fail(text, expected, actual);
    }

    // Run a process, with a timeout, returning the exit code or -1.
    int runProcess(ProcessBuilder pb) throws Exception
    {
        int exit = -1;
        Process p = pb.start();
        for (int t=0; t<timeoutSeconds*5; t++)
        {
            Thread.sleep(1000/5);
            try { exit = p.exitValue(); break; }
            catch (IllegalThreadStateException e) { continue; }
        }
        return exit;
    }

    // Print out the max mark for the assessment.
    void doMax(String text)
    {
        if (maxPrinted) return;
        System.out.println("Max mark: " + toString(max));
        maxPrinted = true;
    }

    // Report the total mark.
    void doTotal(String student, String text)
    {
        if (createSummary)
        {
            marks.println(student + ", " + round(total));
        }
        out.print("<p class='total'>Total mark: ");
        out.print(round(total));
        out.print(" out of ");
        out.print(toString(max));
        out.println("</p>");
    }

    // Pass a test
    void pass(String test, long mark)
    {
        total = plus(total, mark);
        out.print("<pre>");
        out.print("<span class='test'>");
        out.print(verbatim(test));
        out.print("</span> ");
        out.print("<span class='pass'>pass</span>");
        out.println("</pre>");
    }

    // Fail a test becase the program crashes, or succeeds when it shouldn't
    void failExit(String test, String actual, boolean crash)
    {
        out.print("<pre>");
        out.print("<span class='test'>");
        out.print(verbatim(test));
        out.print("</span> ");
        out.println("<span class='fail'>fail</span>");
        out.print("<span class='output'>");
        if (crash)
            out.println("----- crashes with output -------");
        else
            out.println("----- no System.exit(1) ---------");
        out.print(verbatim(actual));
        out.println("---------------------------------");
        out.println("<span></pre>");
    }

    // Fail a test
    void fail(String test, String expected, String actual)
    {
        out.print("<pre>");
        out.print("<span class='test'>");
        out.print(verbatim(test));
        out.print("</span> ");
        out.println("<span class='fail'>fail</span>");
        out.print("<span class='output'>");
        out.println("----- output --------------------");
        out.print(verbatim(actual));
        if (! expected.equals(""))   
        {
            if (isPattern)
                out.println("----- expected output pattern ---");
            else
                out.println("----- expected output -----------");
            out.print(verbatim(expected));
        }
        out.println("---------------------------------");
        out.println("<span></pre>");
    }

    // Convert the special characters in a plain string to HTML entities
    String verbatim(String s)
    {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        return s;
    }

    // Read a file into a list of lines
    List<String> readLines(File file) throws IOException
    {
        Charset UTF8 = StandardCharsets.UTF_8;
        if (! file.exists()) return new ArrayList<String>();
        return Files.readAllLines(file.toPath(), UTF8);
    }

    // Read a file into a string
    String readString(File file) throws IOException
    {
        List<String> lines = readLines(file);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) sb.append(line).append("\n");
        return sb.toString();
    }

    // Support for rationals n/d stored in longs as (n << 32) + d.
    // Pack and unpack rationals
    int numerator(long x) { return (int) (x >> 32); }
    int denominator(long x) { return (int) x; }
    long rat(int n) { return rat(n, 1); }
    long rat(int n, int d)
    {
        int g = gcd(n, d);
        n = n / g;
        d = d / g;
        return ((long)n << 32) + d;
    }

    // Create rational from "n" or "n/d" 
    long rat(String s)
    {
        String[] parts = s.split("/");
        int n = Integer.parseInt(parts[0]);
        int d = 1;
        if (parts.length > 1) d = Integer.parseInt(parts[1]);
        return rat(n, d);
    }

    // Test equality of two rationals
    boolean equals(long x, long y)
    {
        int xn = numerator(x), xd = denominator(x);
        int yn = numerator(y), yd = denominator(y);
        return xn == yn && xd == yd;
    }

    // Add two rationals
    long plus(long x, long y)
    {
        int xn = numerator(x), xd = denominator(x);
        int yn = numerator(y), yd = denominator(y);
        int zn = xn * yd + yn * xd;
        int zd = xd * yd;
        return rat(zn, zd);
    }

    // Round a rational to an integer 
    int round(long x)
    {
        int xn = numerator(x), xd = denominator(x);
        double r = ((double)xn) / xd;
        return (int) Math.round(r);
    }

    // Display a rational
    String toString(long x)
    {
        int xn = numerator(x), xd = denominator(x);
        if (xd == 1) return "" + xn;
        return "" + xn + "/" + xd;
    }

    // Find greatest common divisor
    private int gcd(int m, int n)
    {
        if (n == 0) return m;
        else return gcd(n, m % n);
    }
}
