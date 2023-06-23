import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import static java.lang.System.out;

public class Case2 {
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Задание: \nПользователь вводит месяц и год.\n" +
                "Скачать все снимки за месяц в папку.\n" +
                "Сгенерировать html страницу в этой папке, которая отобразит все скачанные снимки на одной странице. Пример:\n" +
                "<img src=“1.png”/>\n" +
                "<img src=“2.png”/>\n\nРешение: ");

        // Создаем список дат за введённый месяц (из задания Курс валют за месяц Case3_1)
        BufferedReader buffered = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите исходные месяц и год с разделителем '/', пример: 03/2023: ");
        String origMonth = buffered.readLine();  // Start month
        System.out.println();

        // Делаем парсинг введённой строки методом Split.
        String[] items = origMonth.split("/");
        String mon = items[0];
        String yea = items[1];

        int monI = Integer.parseInt(mon);
        int yeaI = Integer.parseInt(yea);

        // Преобразовываем ввод через переменную YearMonth.
        YearMonth ym = YearMonth.of(yeaI, monI);

        int lastDay = ym.lengthOfMonth();

        //    Создаем цикл по дням месяца
        List<String> list_Of_Dates_of_Entered_Month = new ArrayList<>();
        for (int day = 1; day <= lastDay; day++) {
            LocalDate dt = ym.atDay(day);

            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dtStr = dt.format(f);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(dtStr));
            String currentDate;
            currentDate = sdf.format(c.getTime());  // entering current Date

            // Приводим currentDate к формату LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(currentDate, formatter);
            list_Of_Dates_of_Entered_Month.add(String.valueOf(localDate));
        }

        System.out.println(list_Of_Dates_of_Entered_Month);
        System.out.println(); /* Добавляем пустую строку, как разделитель*/
        List<String> fileNames = Arrays.asList();  //   Создаем массив с изображениями

        for (int i = 1; i <= list_Of_Dates_of_Entered_Month.size(); i++) {
            String currentDate = list_Of_Dates_of_Entered_Month.get(i - 1);
            System.out.println(currentDate);

//        Чтобы получить url страницы с нужным нам кодом, берем нужную нам дату, например 2022-01-12 перед ней дописываем '&date='
//        и склеиваем с https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY , т.е.
            String PageWithCodeOfCurrentDate = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY" + "&date=" + currentDate;
            String currentCodeItself = downloadWebPage(PageWithCodeOfCurrentDate);
            System.out.println(PageWithCodeOfCurrentDate);
            System.out.println(currentCodeItself);

            int urlBegin = currentCodeItself.lastIndexOf(",\"url");
            int urlEnd = currentCodeItself.lastIndexOf("}");
            String urlOfCurrentPhoto = currentCodeItself.substring(urlBegin + 8, urlEnd - 1);
            System.out.println(urlOfCurrentPhoto);
            try (InputStream in = new URL(urlOfCurrentPhoto).openStream()) {
                Files.copy(in, Paths.get("NASA_Photos_Of_Month\\" + "image" + i + ".png"), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException exception) {
                out.println("Input/Output error");
            }
        }
        out.println();

        File[] imgFiles = new File("C:\\Users\\User\\IdeaProjectsDrafts\\Draft230513_Case2\\NASA_Photos_Of_Month").listFiles();

        // Объединяем все фото в одну картинку
        int rows = 3;
        int cols = 1;
        int chunks = rows * cols;

        int chunkWidth, chunkHeight;
        int type;

        //creating a buffered image array from image files
        BufferedImage[] buffImages = new BufferedImage[chunks];
        for (int i = 0; i < chunks; i++) {
            buffImages[i] = ImageIO.read(imgFiles[i]);
        }
        type = buffImages[0].getType();
        chunkWidth = buffImages[0].getWidth();
        chunkHeight = buffImages[0].getHeight();

        //Initializing the final image
        BufferedImage finalImg = new BufferedImage(chunkWidth*cols, chunkHeight*rows, type);

        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }
        System.out.println("Изображения объединены");
        ImageIO.write(finalImg, "png", new File("NASA_All_Photos_In_One_html_File\\all_Photos.png"));
    }

    private static String downloadWebPage (String url) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        URLConnection urlConnection = new URL(url).openConnection();
        try (InputStream is = urlConnection.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
