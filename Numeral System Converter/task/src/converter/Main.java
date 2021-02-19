package converter;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        int sourceRadix = 0;
        String sourceNumber = null;
        int targetRadix = 0;

        try {
            sourceRadix = scanner.nextInt();
            sourceNumber = scanner.next();
            targetRadix = scanner.nextInt();
        } catch (Exception e) {
            System.out.print("error");
            System.out.println(e.getMessage());
        }

        if (sourceRadix > 36 || sourceRadix < 1 || targetRadix > 36 || targetRadix < 1) {
            System.out.print("error");
            System.out.println("wrong radix");
        } else if (sourceNumber.matches("\\W\\D")) {
            System.out.print("error");
            System.out.println("wrong number");
        } else {
            String integerBase;
            String fractionBase;
            if (sourceNumber.indexOf(".") > 0) {
                integerBase = sourceNumber.substring(0, sourceNumber.indexOf("."));
                fractionBase = "0" + sourceNumber.substring(sourceNumber.indexOf("."));
            } else {
                integerBase = sourceNumber;
                fractionBase = "0";
            }

            DecimalInteger decimalInteger = new DecimalInteger(integerBase, sourceRadix);
            TargetInteger targetInteger = new TargetInteger();
            System.out.print(targetInteger.toTargetInteger(decimalInteger.toDecimalNumber(), targetRadix));
            Fraction fraction = new Fraction(sourceRadix, targetRadix, fractionBase);
            System.out.println(fraction.convert());
        }
    }
}

class Fraction {
    int baseRadix;
    int targetRadix;
    String baseFraction;
    double decimalValue;

    public Fraction(int baseRadix, int targetRadix, String baseFraction) {
        this.baseRadix = baseRadix;
        this.targetRadix = targetRadix;
        this.baseFraction = baseFraction;
    }

    public String convert() {

        if ("0".equals(baseFraction)) {
            return "";
        } else {
            decimalValue = 0;
            for (int i = 2; i < baseFraction.length(); i++) {
                decimalValue += Character.getNumericValue(baseFraction.charAt(i)) / Math.pow(baseRadix, (i-1));
            }
        }

        StringBuilder targetFraction = new StringBuilder();
        targetFraction.append(".");
        TargetInteger targetInteger = new TargetInteger();
        double supportFraction = decimalValue;
        int supportValue;
        for (int i = 0; i < 5; i++) {
            supportFraction *= targetRadix;
            supportValue = (int) supportFraction;
            supportFraction -= supportValue;
            targetFraction.append(targetInteger.toTargetInteger(Integer.toString(supportValue), targetRadix));
        }
        return targetFraction.toString();
    }
}

class DecimalInteger {
    private String sourceNumber;
    private int baseRadix;

    public DecimalInteger(String sourcenumber, int baseRadix) {
        this.sourceNumber = sourcenumber;
        this.baseRadix = baseRadix;
    }

    public String toDecimalNumber () {
        if (this.baseRadix == 10) {
            return this.sourceNumber;
        } else if (this.baseRadix == 1) {
            return Integer.toString(this.sourceNumber.length());
        } else {
            return Integer.toString(Integer.parseInt(this.sourceNumber, this.baseRadix));
        }
    }
}

class TargetInteger {

    public String toTargetInteger(String decimalNumber, int targetRadix) {
        if (targetRadix == 10) {
            return decimalNumber;
        } else if (targetRadix == 1) {
            StringBuilder decimal = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(decimalNumber); i++) {
                decimal.append(1);
            }
            return decimal.toString();
        } else {
            return Integer.toString(Integer.parseInt(decimalNumber), targetRadix);
        }
    }
}

/* SOLUTION FROM HYPERSKIL stage #5

package converter;

import java.util.Scanner;

class Num {
    long intPart;
    double fracPart;
    String intString;
    String fracString;
    boolean isToOne;
    boolean isWithFrac;
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int radixOfNum = sc.nextInt();
        String numInString = sc.next();
        int radixTarget = sc.nextInt();

        int roundUpFive = 5;
        Num num = new Num();

        if (numInString.contains(".")) {
            num.isWithFrac = true;
            int point = numInString.indexOf(".");
            num.intString = numInString.substring(0, point);
            num.fracString = numInString.substring(point + 1);
        } else {
            num.intString = numInString;
        }

        if (radixOfNum == 1) {
            num.intPart = num.intString.length();
        } else {
            num.intPart = Long.parseLong(num.intString, radixOfNum);
        }

        if (radixTarget == 1) {
            num.isToOne = true;
            for (int i = 0; i < num.intPart; i++) {
                System.out.print(1);
            }
        } else {
            String intTarget = Long.toString(num.intPart, radixTarget);
            System.out.print(intTarget);
        }

        if (!num.isToOne && num.isWithFrac) {
            char[] digits = num.fracString.toCharArray();
            StringBuilder fracTarget = new StringBuilder();
            fracTarget.append(".");

            for (int i = 0; i < digits.length; i++) {
                double partOfFrac =
                        Character.digit(digits[i], radixOfNum)
                                / Math.pow(radixOfNum, i + 1);
                num.fracPart += partOfFrac;
            }

            for (int i = 0; i < roundUpFive; i++) {
                double fracToTargetRadix = num.fracPart * radixTarget;
                long intPartOfFrac = (long) fracToTargetRadix;
                String intPartOfFracStr = Long.toString(intPartOfFrac, radixTarget);
                num.fracPart = fracToTargetRadix - intPartOfFrac;
                fracTarget.append(intPartOfFracStr);
            }

            System.out.print(fracTarget.toString());
        }
    }
}

another interested solution stage #6
package converter;

import java.util.Scanner;

public class Main {

    static final int OUT_PRECISION = 5;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input;

        int radixIn = 0;
        if (sc.hasNext("[0-9]{1,2}")) {
            radixIn = sc.nextInt();
        } else {
            System.out.println("Error! Valid source radix not provided!");
            return;
        }

        if (radixIn < 1 || radixIn > Character.MAX_RADIX) {
            System.out.println("Error! Source radix out of range!");
            return;
        }

        String numberInString = "0.0";
        if (sc.hasNext("[0-9A-Za-z]+(.[0-9A-Za-z]+)?")) {
            numberInString = sc.next().strip();
        } else {
            System.out.println("Error! Valid source number not provided!");
            return;
        }

        boolean isFractional = numberInString.indexOf('.') == -1 ? false : true;

        // if given number isFractional, the String array will contain:
        // numberPartStrings[0] - integer part,
        // numberPartStrings[1] - fractional part,
        //
        String[] numberPartStrings = isFractional ? numberInString.split("\\.") : null;

        int radixOut = 0;
        if (sc.hasNext("[0-9]{1,2}")) {
            radixOut = sc.nextInt();
        } else {
            System.out.println("Error! Valid target radix not provided!");
            return;
        }

        if (radixOut < 1 || radixOut > Character.MAX_RADIX) {
            System.out.println("Error! Target radix out of range!");
            return;
        }

        long numberL = 0;
        double numberD = 0.0;

        if (!isFractional) {
            if (radixIn == 1) {
                numberL = numberInString.length();
            } else {
                numberL = Integer.parseInt(numberInString, radixIn);
            }
        } else { // isFractional == true
            if (radixIn == 1) {
                System.out.println("Error! The number with radix 1 must NOT be fractional!");
                return;
            } else {
                numberD = Integer.parseInt(numberPartStrings[0], radixIn);
                int noOfSourceFractionalDigits = numberPartStrings[1].length();
                numberD += Integer.parseInt(numberPartStrings[1], radixIn) / Math.pow(radixIn, noOfSourceFractionalDigits);
            }
        }

        String numberOutString;

        if (!isFractional) {
            if (radixOut == 1) {
                StringBuilder sb = new StringBuilder((int) numberL);
                for (int i = 0; i < numberL; i++) {
                    sb.append('1');
                }
                numberOutString = sb.toString();
            } else {
                numberOutString = Long.toString(numberL, radixOut);
            }
        } else { // isFractional == true
            if (radixOut == 1) {
                StringBuilder sb = new StringBuilder((int) numberD);
                for (int i = 0; i < (int) numberD; i++) {
                    sb.append('1');
                }
                numberOutString = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder(Long.toString((long) numberD, radixOut));
                sb.append('.');
                double fraction = numberD - (long) numberD;
                int nextFractionSymbol = 0;
                for (int i = 0; i < OUT_PRECISION; i++) {
                    nextFractionSymbol = (int) (fraction * radixOut);
                    sb.append(Long.toString(nextFractionSymbol, radixOut));
                    fraction = fraction * radixOut - nextFractionSymbol;
                }
                numberOutString = sb.toString();
            }
        }

        System.out.println(numberOutString);
    } // psv main()
} // class Main
* */