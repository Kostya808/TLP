// // using System;

// // namespace ConsoleApplication2
// // {
public class Program 
{
    public static void Main() 
    {
       int a = /* */15; //комментарий
       int b;
       b = 5; /*Комментарий*/
        /*Комментарий
        КОММЕНТАРИЙ
        КОММЕНТАРИЙ!!!
        */
        for /*И ЗДЕСЬ КОММЕНТАРИЙ*/ (int i=a; i > 0; i--)
        {
            if (a % i == 0)
            {
                if (b % i == 0)
                {
                    Console.WriteLine(i);
                    Console.ReadLine();
                    i = 0;
                }
            }
        }
        string Out = "Добро пожаловать в C#!))))))))))";
        Console.WriteLine(Out);
    }
}
// // }