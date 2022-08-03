package demo;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy EEEE")));
        System.out.println(date.isAfter(LocalDate.of(2022, 1, 1)));
    }
}
