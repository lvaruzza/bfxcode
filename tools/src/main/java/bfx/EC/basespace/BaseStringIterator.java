package bfx.EC.basespace;

/**
 * @author Jan Schroeder (original code)
 * @author Andreas Bremges (modifications)
 */
import java.util.Iterator;

public class BaseStringIterator implements Iterator<String> {

    private char[] currentString;

    public BaseStringIterator() {
        this.currentString = new char[]{'A', 'A', 'A', 'A', 'A'};
    }

    public boolean hasNext() {
        if (String.valueOf(currentString).equals("TTTTT")) {
            return false;
        }
        return true;
    }

    public void remove() {
    }

    public String next() {

        for (int i = 0; i < currentString.length; i++) {
            if (currentString[i] == 'T') {
                currentString[i] = 'A';
            } else {
                switch (currentString[i]) {
                    case 'A':
                        currentString[i] = 'C';
                        break;
                    case 'C':
                        currentString[i] = 'G';
                        break;
                    case 'G':
                        currentString[i] = 'T';
                        break;
                    case 'N':
                        currentString[i] = 'N';
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
