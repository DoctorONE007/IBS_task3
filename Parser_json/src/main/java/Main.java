import com.google.gson.Gson;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Scanner;

public class Main {
    static LocalDate userdate;

    public static void main(String[] args) {
        Gson gson = new Gson();
        Companies companies = null;
        try (FileReader reader = new FileReader("task.json")) {
            companies = gson.fromJson(reader, Companies.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        assert companies != null;
        List<Company> companiesList = (companies.companies);

        System.out.println("--------------------------");

        companiesList.forEach(company -> {
            String date = LocalDate.parse(
                    company.founded,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
            ).format(DateTimeFormatter.ofPattern("dd/MM/yy"));
            System.out.println(company.name + " - Дата основания " + date);
        });

        System.out.println("--------------------------");

        companiesList.forEach(company -> company.securities.stream().filter(securities -> {
            LocalDate date = LocalDate.parse(securities.date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            return date.isBefore(LocalDate.now());
        }).forEach(securities -> System.out.println("Код - " + securities.code + " Дата истечения - " + securities.date + " Название организации-владельца - " + securities.name)));

        int count = companiesList.stream().mapToInt(company -> (int) company.securities.stream().filter(securities -> {
            LocalDate date = LocalDate.parse(securities.date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            return date.isBefore(LocalDate.now());
        }).count()).sum();

        System.out.println("Количество просроченных бумаг - " + count);
        System.out.println("--------------------------");
        System.out.println("Введите дату, для вывода организаций основанных после нее");
        Scanner scanner = new Scanner(System.in);
        String date;
        do {
            date = scanner.nextLine();
        } while (!CheckDate(date));

        companiesList.stream().filter(company -> {
            LocalDate localDate = LocalDate.parse(company.founded, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            return localDate.isAfter(userdate);
        }).forEach(company -> System.out.println(company.name + " " + company.founded));

        System.out.println("--------------------------");
        System.out.println("Введите код валюты для вывода ценных бумаг");
        String money = scanner.nextLine();
        count = companiesList.stream().mapToInt(company -> (int) company.securities.stream()
                .filter(securities -> securities.currency.contains(money.toUpperCase())).count()).sum();
        if (count > 0) {
            companiesList.forEach(company -> company.securities.stream()
                    .filter(securities -> securities.currency.contains(money.toUpperCase()))
                    .forEach(securities -> System.out.println(company.id + " " + securities.code)));
        } else
            System.out.println("По вашему запросу ничего не найдено");


    }

    public static boolean CheckDate(String date) {
        DateTimeFormatter mdy = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter parser = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd.MM.yy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yy"))
                .toFormatter();
        try {
            mdy.format(LocalDate.parse(date, parser));
        } catch (Exception e) {
            System.out.println("Неверный формат даты");
            System.out.println("Попробуйте еще раз");
            return false;
        }
        if(date.split("")[date.length() - 3].equals(".") ||date.split("")[date.length() - 3].equals("/") ){
            date = date.substring(0,date.length()-2) + "19" + date.substring(date.length()-2,date.length());
        }
        userdate = LocalDate.parse(date, parser);
        return true;
    }
}