// using System;

// namespace LoopTasks
// {
public class Program
{
    public static void Main(string[] args)
    {
        int NumbersCount = 100;
        int[] numbers = new int[NumbersCount];

        for (int i = 0; i < NumbersCount; i++)
            numbers[i] = i + 1;

        int min = numbers[0];
        int minIndex = 0;
        
        for (int i = 0; i < NumbersCount; i++)
        {
            if (min > numbers[i])
            {
                min = numbers[i];
                minIndex = i;
            }
        }
        Console.Write(numbers[minIndex]);
    }
}
// }