public class main	{

	public static void main( String[] args )	{
		Animal[] aGroup =  new Animal[10];

		addAnimal(createDog(4,Fur.BROWN),aGroup);
		addAnimal(createFish(0,Scales.SHINY),aGroup);
		printAnimalCoords(aGroup);
		moveAnimalsBack(aGroup);
		printAnimalCoords(aGroup);
	}

	public static void addAnimal(Animal a, Animal[] aArr)	{
		int i;
		for(i = 0; aArr[i] != null;i++){
		/*do nothing*/	
		}

		try{
			if(aArr[i] == null)	{
				aArr[i] = a;
			}
		} 
		catch(ArrayIndexOutOfBoundsException e)	{
			System.out.println(e);
		}

	}

	public static Dog createDog(int legs, Fur fType)	{
		Dog dog_1 = new Dog(legs, fType);
		return dog_1;
	}

	public static Fish createFish(int nlegs, Scales sType)	{
		Fish fish_1 = new Fish(nlegs, sType);
		return fish_1;
	}

	public static void moveAnimalsBack(Animal[] aArr)	{
		for(int i = 0; aArr[i] != null; i++)	{
			aArr[i].moveBack();
		}
	}

	public static void printAnimalCoords(Animal[] aArr)	{
		for(int i = 0; aArr[i] != null; i++)	{
			aArr[i].printPosition();
		}
	}
}
