package bfx.EC.colorspace;

/**
 * @author Jan Schroeder (original code)
 * @author Andreas Bremges (modifications)
 * @author Leena Salmela (modifications for color space)
 */
import java.util.Iterator;

public class ColorStringIterator implements Iterator {

    private char[] currentString;

    public ColorStringIterator() {
        this.currentString = new char[]{'0', '0', '0', '0', '0'};
    }

    public boolean hasNext() {
        if (String.valueOf(currentString).equals("33333")) {
            return false;
        }
        return true;
    }

    public void remove() {
    }

    public String next() {

        for (int i = 0; i < currentString.length; i++) {
            if (currentString[i] == '3') {
                currentString[i] = '0';
            } else {
                switch (currentString[i]) {
                    case '0':
                        currentString[i] = '1';
                        break;
                    case '1':
                        currentString[i] = '2';
                        break;
                    case '2':
                        currentString[i] = '3';
                        break;
                    case '3':
                        currentString[i] = '.';
                        break;
                    default:
                        break;
                }
                break;
            }
        }

        return String.valueOf(currentString);
    }
}
