public class Program
{
    public static void Main()
    {
        int[] numbers = new int[5];

        for (int i = 0; i < numbers.Length; i++) {
            Console.Write("Введите ");
            Console.Write(i);
            Console.Write(" элемент массива: ");
            numbers[i] = Console.ReadLine();
        }
        int min = numbers[0];
        int minIndex = 0;
        
        for (int i = 0; i < numbers.Length; i++)
        {
            if (min > numbers[i])
            {
                min = numbers[i];
                minIndex = i;
            }
        }
        Console.Write("Минимальный элемент массива: ");
        Console.WriteLine(numbers[minIndex]);
    }
}
