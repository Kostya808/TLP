// // // using System;

// // namespace ConsoleApplication2
// // {
public class Program 
{
    public static void Main() 
    {
       int a = 15, b = 5;
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
        print_message("Добро пожаловать в  C#!", s, d, 1, 2, 3, 2.0);
    }

    public void print_message(string Out, int s, int d) {
        Console.WriteLine(Out);
    }
}
// // }

// int a, b = 2, c;

// public static int gg(int k, string f, double f) {}
