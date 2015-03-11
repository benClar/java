import com.bclarke.testing.*;
import com.bclarke.general.*;
import java.util.* ;
import java.io.*;


public class Database  {

	HashMap<String, Table> tables;
	String databaseName;

	private final String TABLE_INDEX_FILE = ".dbTableIndex.txt";
	private final String DATABASE_ROOT_DIRECTORY = "resources";

	public Database(String name)	{
		databaseName = name; 
		tables = new HashMap<String, Table>();
	}

	public void writeDatabase()	{
		createDatabaseFolder();
		writeDatabaseIndex();
		writeTables();
	}

	private void writeTables()	{
		Map<String, Table> map = tables;
		TableWriter tw = new TableWriter();
		for(Table tValue : map.values())	{
			tw.writeTable(tValue,getDatabaseName());
		}
	}

	private Scanner openIndexFile()	{
		File indexFile = new File(DATABASE_ROOT_DIRECTORY + "/" + getDatabaseName() + "/" + TABLE_INDEX_FILE);
		try	{
			if(indexFile.exists())	{
				Scanner scannedIndexFile = new Scanner(indexFile);
				return scannedIndexFile;

			} else {
				throw new Exception();
			}
		} catch (Exception e)	{
			WhiteBoxTesting.catchFatalException(e,"Table Index File not found");
			return null;
		}
	}

	private int createDatabaseFolder()	{
		if(!databaseExists())	{
			File databaseDir = new File(DATABASE_ROOT_DIRECTORY + "/" + getDatabaseName() + "/");
			databaseDir.mkdir();
			return 1;
		} 
		return 0;
	}

	private void writeDatabaseIndex()	{
		Map<String, Table> map = tables;
		try	{			
			BufferedWriter tableIndex = new BufferedWriter(new FileWriter(DATABASE_ROOT_DIRECTORY + "/" + getDatabaseName() + "/" + TABLE_INDEX_FILE));
			for(String key : map.keySet())	{
				tableIndex.write(key);
				tableIndex.newLine();
			}
			tableIndex.close();
		} catch (IOException e)	{
			WhiteBoxTesting.catchFatalException(e,"IOException");
		}

	}

	private boolean databaseExists()	{
		File f = new File(DATABASE_ROOT_DIRECTORY + "/" + getDatabaseName());
		if(f.exists() && f.isDirectory())	{
			return true;
		} else	{
			return false;
		}
	}

	public String getDatabaseName()	{
		return databaseName;
	}

	public int addTable(Table newTable)	{
		try	{
			if(databaseContainsTable(newTable.getTableName()) == false)	{
				tables.put(newTable.getTableName(),newTable);
				return 1;
			} else	{
				throw new Exception();
			}
		} catch (Exception e)	{
			return WhiteBoxTesting.catchException(e,"Table name already exists");
		}
	}

	public void populateDatabase()	{
		if(databaseExists())	{
			Scanner indexFile = openIndexFile();
			TableReader newTable = new TableReader();
			try	{
				while(indexFile.hasNext())	{
					if(addTable(newTable.readTable(getDatabaseName(),indexFile.nextLine())) == 0)	{
						throw new Exception();
					}
				}
				indexFile.close();
			} catch (Exception e)	{
				WhiteBoxTesting.catchFatalException(e,"Duplicate table on file.");
			}
		}
	}

	private boolean tableFileExists(String table)	{
		System.out.println(getTableFileLocation(table));
		File f = new File(getTableFileLocation(table));
		if(f.exists() && !f.isDirectory())	{
			return true;
		} else	{
			return false;
		}

	}


	public int changeTableName(String oldName, String newName)	{
		try	{
			if(databaseContainsTable(newName) == false)	{
				if(getTable(oldName).setTableName(newName) == 1	){
					 if(changeTableMapping(oldName, newName) == 1)	{
					 	if(tableFileExists(oldName))	{
					 		System.out.println("OOOOOLD FILLLLE EXISTSSSS");
					 		renameTableFile(oldName,newName);
					 	}
					 	return 1;
					 } 
				} 
			} else {
				throw new Exception(" Table" + newName + "Already exists ");
			}
		} catch (NullPointerException e)	{
			return WhiteBoxTesting.catchException(e,"Table" + oldName + "does not exist");
		} catch (Exception e)	{
			return WhiteBoxTesting.catchException(e," Could not create Table ");
		}
		return 0;
	} 

	private String getTableFileLocation(String tName)	{
		return (DATABASE_ROOT_DIRECTORY + "/" + getDatabaseName() + "/" + tName + ".txt");
	}

	private int renameTableFile(String oldName, String newName)	{
		File tableFile = new File(getTableFileLocation(oldName));
		try	{
			if(tableFile.renameTo(new File(getTableFileLocation(newName))) == false)	{
				throw new Exception();
			} 

			System.out.println("RENAMED");
			return 1;
		} catch (Exception e)	{
			return WhiteBoxTesting.catchException(e,"Table File could not be renamed.");
		}
	}

	public Table getTable(String tableName)	{
		return tables.get(tableName);
	}

	private int changeTableMapping(String oldName, String newName)	{
		try	{
			Table tableToChange = tables.get(oldName);
			if (tableToChange == null)	{
				throw new Exception();
			}
			tables.remove(oldName);
			return addTable(tableToChange);
		} catch (Exception e){
			return WhiteBoxTesting.catchFatalException(e,"Table with null value stored in Database");
		}
	}

	private int deleteTableFile(String tableName)	{
		File tableToDelete = new File(getTableFileLocation(tableName));
		try	{
			if(!tableToDelete.delete()){
				throw new Exception();
			}
			return 1;
		} catch (Exception e)	{
			return WhiteBoxTesting.catchException(e,"Unable to delete table file " + tableName);
		}
	}

	private int removeTable(String tName)	{
		try { 
			if(databaseContainsTable(tName))	{
				if(tableFileExists(tName))	{
					deleteTableFile(tName);
				}
				return 1;
			} else	{
				throw new Exception();
			}
		} catch(Exception e)	{
			return WhiteBoxTesting.catchException(e,"Table does not exist to delete");
		}
	}

	private boolean databaseContainsTable(String tableName)	{
		return tables.containsKey(tableName);
	}

/*----------Testing----------*/

	public static void main( String[] args )    {
	 
		if(WhiteBoxTesting.checkMode(args).equals(OperatingMode.UNIT_TEST)) {
			Database.unitTest(new Testing()).endTesting();
		} 
	}

	public static Testing unitTest(Testing t)	{
		WhiteBoxTesting.startTesting();
		Database.unitTest_AddTableToDatabase(t);
		Database.unitTest_databaseIO(t);
		return t;
	}

	public static Testing unitTest_databaseIO(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Database Unit Tests: Database I/O");
		String[] cNames = new String[]{"col1","col2","col3"};
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];
		Field[] f = new Field[3];


		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;



		Table tab1=new Table(cNames,dtype,ktype,"Appliances");
		Table tab2=new Table(cNames,dtype,ktype,"Companies");
		Table tab3=new Table(cNames,dtype,ktype,"Countries");
		Database dbWrite = new Database("testWriteDatabase");
		Database dbRead = new Database("testReadDatabase");
		dbWrite.addTable(tab1);
		dbWrite.addTable(tab2);
		dbWrite.addTable(tab3);


		f[0] = new Field("1",FieldDataType.STRING);
		f[1] = new Field("Sony",FieldDataType.STRING);
		f[2] = new Field("TV",FieldDataType.STRING);
		dbWrite.getTable("Appliances").addRecord(f);

		f[0] = new Field("1",FieldDataType.STRING);
		f[1] = new Field("Sony",FieldDataType.STRING);
		f[2] = new Field("Japan",FieldDataType.STRING);
		dbWrite.getTable("Companies").addRecord(f);

		f[0] = new Field("1",FieldDataType.STRING);
		f[1] = new Field("Asia",FieldDataType.STRING);
		f[2] = new Field("Japan",FieldDataType.STRING);
		dbWrite.getTable("Countries").addRecord(f);

		
		dbWrite.writeDatabase();

		dbRead.databaseName = "testWriteDatabase";
		dbRead.populateDatabase();
		t.compare(dbRead.getTable("Appliances").getFieldValueByColumnName("1","col2"),"==","Sony","Column 2 record 1 equal to Sony from read Appliances table");
		t.compare(dbRead.getTable("Appliances").getFieldValueByColumnName("1","col2"),"==",dbWrite.getTable("Appliances").getFieldValueByColumnName("1","col2"),"Appliance Table column 2 are equal from written and read table");
		t.compare(dbRead.getTable("Countries").getFieldValueByColumnName("1","col2"),"==","Asia","Column 2 record 1 equal to Asia from read Countries table");
		t.compare(dbRead.getTable("Countries").getFieldValueByColumnName("1","col2"),"==",dbWrite.getTable("Countries").getFieldValueByColumnName("1","col2"),"Countries table Column 2 record 1 equal from written and read table");
		t.compare(true,"==",dbWrite.tableFileExists("Companies"),"Companies table file exists");
		dbWrite.changeTableName("Companies","Corporations");
		System.out.println("JUST CHANGED TABLE NAME");
		t.compare(false,"==",dbWrite.tableFileExists("Companies"),"Companies does not exist table file exists");
		t.compare(true,"==",dbWrite.tableFileExists("Corporations"),"Corporations table file exists");

		//dbWrite.deleteTableFile(dbWrite.getTable("Appliances").getTableName());
		t.exitSuite();
		return t;
	}

	public static Testing unitTest_AddTableToDatabase(Testing t)	{
		WhiteBoxTesting.startTesting();
		t.enterSuite("Database Unit Tests: Adding Table to database");
		String[] cNames = new String[]{"col1","col2","col3"};
		FieldDataType[] dtype = new FieldDataType[3];
		FieldDataType[] ktype = new FieldDataType[3];

		for(int i = 0; i < dtype.length; i++)	{
			dtype[i] = FieldDataType.STRING;
			ktype[i] = FieldDataType.NONKEY;
		}
		ktype[0] = FieldDataType.PKEY;

		Table tab=new Table(cNames,dtype,ktype,"databaseTestTable");
		Table tab2=new Table(cNames,dtype,ktype,"databaseTestTable2");

		Database db = new Database("testDatabase");

		t.compare(db.addTable(tab),"==",1,"Adding valid table to database");
		t.compare(db.addTable(tab),"==",0,"Adding duplicate invalid table to database");
		t.compare(db.changeTableName("databaseTestTable","renamedTestTable"),"==",1,"Successfully renamed Table to renamedTestTable");
		t.compare("renamedTestTable","==",db.getTable("renamedTestTable").getTableName(),"Name Change cascaded to Table object successfully: renamedTestTable");
		t.compare(db.changeTableName("renamedTestTable","databaseTestTable"),"==",1,"Successfully renamed Table to databaseTestTable");
		t.compare("databaseTestTable","==",db.getTable("databaseTestTable").getTableName(),"Name Change cascaded to Table object successfully: databaseTestTable");
		t.compare(db.changeTableName("databaseTestTable",null),"==",0,"table cannot be called null");
		t.compare(db.changeTableName("databaseTestTable","databaseTestTable"),"==",0,"table cannot be renamed to the same name");
		db.addTable(tab2);
		t.compare(db.changeTableName("databaseTestTable2","databaseTestTable"),"==",0,"Table with this name already exists in database");
		t.compare(false,"==",db.tableFileExists(tab.getTableName()),"nonexistentFile does not exist");
		t.exitSuite();
		return t;
	}

	/*To do:
		create ability to rename files
		create ability to delete files 
		remember to validate if database already exists before allowing its creation
		*/
}

