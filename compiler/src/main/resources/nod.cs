public class Program 
{
    public static void Main() 
    {
        Console.WriteLine("Введите а: ");    	
        int a = Console.ReadLine();
        Console.WriteLine("Введите b: ");    	
        int b = Console.ReadLine();
        for (int i=a; i > 0; i--)
        {
            if (a % i == 0)
            {
                if (b % i == 0)
                {
                    Console.WriteLine("Наибольший общий делитель: ");
                    Console.WriteLine(i);
                    i = 0;
                }
            }
        }
    }
}