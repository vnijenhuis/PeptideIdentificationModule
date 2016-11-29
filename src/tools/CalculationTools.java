/*
 * @author Vikthor Nijenhuis
 * @project Peptide mzIdentML Identfication Module * 
 */
package tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Class containing tools for calculation purposes.
 *
 * @author vnijenhuis
 */
public class CalculationTools {

    /**
     * Can round Double values.
     *
     * @param doubleToRound Double value.
     * @param decimals amount of decimals to round to.
     * @return rounded Double value.
     */
    public Double roundDouble(Double doubleToRound, Integer decimals) {
        BigDecimal bd = new BigDecimal(doubleToRound);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        Double roundedDouble = bd.doubleValue();
        return roundedDouble;
    }
}
