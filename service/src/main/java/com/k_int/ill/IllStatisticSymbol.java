package com.k_int.ill;

/**
 * Defines the ill statistic for a symbol
 */
public class IllStatisticSymbol {
    static public final IllStatisticSymbol defaultStatistics = new IllStatisticSymbol(
        1,
        1,
        0,
        0,
        "Unable to determine statistics"
    );

    /** When the statistic was taken */
    public long timestamp;

    /** The loan ratio */
    public long libraryLoanRatio;

    /** The borrowing ratio */
    public long libraryBorrowRatio;

    /** The number of items the library has on ill loan */
    public long currentLoanLevel;

    /** The number of items the library is currently borrowing through ill */
    public long currentBorrowingLevel;

    /** The reason as to how these statistics were generated */
    public String reason;

    public IllStatisticSymbol(
        long libraryLoanRatio,
        long libraryBorrowRatio,
        long currentLoanLevel,
        long currentBorrowingLevel,
        String reason
    ) {
        timestamp = System.currentTimeMillis();
        this.libraryLoanRatio = libraryLoanRatio;
        this.libraryBorrowRatio = libraryBorrowRatio;
        this.currentLoanLevel = currentLoanLevel;
        this.currentBorrowingLevel = currentBorrowingLevel;
        this.reason = reason;
    }
}
