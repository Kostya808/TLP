// using System;
 
// namespace ConsoleApplication1
// {
public class Program
{
    public static void Main(string[] args)
    {
        string stroka = "abcdef", podstroka = "bcd";
        int count = 0, i, j = 0;
        for(i = 0; i < stroka.Length; i++) {
            if(podstroka[j] == stroka[i]) {
                j++;
                count++;
            }
            else {
                j = 0;
                count = 0;
            }
            if(count == podstroka.Length) {
                Console.Write("Является подстрокой!");
                i = stroka.Length;
            }
        }
    }
}
// }