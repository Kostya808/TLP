public class Program
{
    public static void Main()
    {   
        int[] numbers = new int[5];
        int min, minIndex;

        Console.WriteLine("Введите 5 элементов массива:");
        
        for (int i = 0; i < numbers.Length; i++) {
            numbers[i] = init_array_element("Введите элемент ", i);
        }
        
        min = numbers[0];
        minIndex = 0;
        
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

    public static int init_array_element(string message, int number_elemnet) {
        int return_value;
        Console.Write(message);
        Console.Write(number_elemnet);
        Console.Write(": ");
        return_value = Console.ReadLine();        
        return return_value;
    }
}