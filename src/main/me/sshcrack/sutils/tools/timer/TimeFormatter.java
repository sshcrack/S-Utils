package me.sshcrack.sutils.tools.timer;

import me.sshcrack.sutils.message.MessageManager;

public class TimeFormatter {
    private static final String dateFormat = MessageManager.getMessage("timer.units.dateFormat");
    private static final String hourFormat = MessageManager.getMessage("timer.units.hourFormat");
    private static final String minuteFormat = MessageManager.getMessage("timer.units.minuteFormat");

    private static Integer[] divideWithRemainder(double num, double div) {
        return new Integer[]{
                (int) Math.floor(num / div),
                (int) (num % div)
        };
    }

    private static String formatNumber(int number) {
        return (number > 9) ? String.valueOf(number) : "0" + number;
    }

    public static String formatTime(long millis) {

        long secondsInMillis = 1000;
        long minuteInMillis = secondsInMillis * 60;
        long hourInMillis = minuteInMillis * 60;
        long dayInMillis = hourInMillis * 24;

        Integer[] daysPart = divideWithRemainder(millis, dayInMillis);
        int rawDays = daysPart[0];
        int daysRest = daysPart[1];

        Integer[] hoursPart = divideWithRemainder(daysRest, hourInMillis);
        int rawHours = hoursPart[0];
        int hoursRest = hoursPart[1];

        Integer[] minutesPart = divideWithRemainder(hoursRest, minuteInMillis);
        int minuteRest = minutesPart[1];

        Integer[] secondsPart = divideWithRemainder(minuteRest, secondsInMillis);


        String days = formatNumber(daysPart[0]);
        String hours = formatNumber(hoursPart[0]);
        String minutes = formatNumber(minutesPart[0]);
        String seconds = formatNumber(secondsPart[0]);

        if (rawDays == 0 && rawHours == 0)
            return String.format(minuteFormat, minutes, seconds);

        if (rawDays == 0)
            return String.format(hourFormat, hours, minutes, seconds);

        return String.format(dateFormat, days, hours, minutes, seconds);
    }
}
