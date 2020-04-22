// // // using System;

// // namespace ConsoleApplication2
// // {
public class Program 
{
    public static void Main() 
    {
       int a = 15; 
       int b;
       b = 5; 

        for (int i=a; i > 0; i--)
        {
            if (a % i == 0)
            {
                if (b % i == 0)
                {
                    int a;
                    Console.WriteLine(i);
                    Console.ReadLine();
                    i = 0;
                }
            }
        }
        string Out = "Добро пожаловать в C#!)";
        Console.WriteLine(Out);
    }
}
// // }

// int a, b = 2, c;

// public int gg(int k, string f, double f) {}
