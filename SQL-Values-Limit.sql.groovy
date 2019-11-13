LIMIT = 5000
SEP = ", "
QUOTE = "\'"
NEWLINE = System.getProperty("line.separator")

KEYWORDS_LOWERCASE = com.intellij.database.util.DbSqlUtil.areKeywordsLowerCase(PROJECT)
KW_INSERT_INTO = KEYWORDS_LOWERCASE ? "insert into " : "INSERT INTO "
KW_VALUES = KEYWORDS_LOWERCASE ? ") values " : ") VALUES "
KW_NULL = KEYWORDS_LOWERCASE ? "null" : "NULL"

OUT.append(KW_INSERT_INTO)
if (TABLE == null) OUT.append("MY_TABLE")
else OUT.append(TABLE.getParent().getName()).append(".").append(TABLE.getName())

OUT.append(" (")

COLUMNS.eachWithIndex { column, idx ->
    OUT.append(column.name()).append(idx != COLUMNS.size() - 1 ? SEP : "")
}

OUT.append(KW_VALUES).append(NEWLINE)

Collection ROWSCOL = new ArrayList()
for (Object t : ROWS)
    ROWSCOL.add(t);

LIMITCNT = 0

ROWSCOL.eachWithIndex { dataRow, rIdx ->
    LIMITCNT++
    ENDSTATEMENT = false
    WRITEHEADER = false
    if (LIMITCNT == LIMIT) {
        LIMITCNT = 0
        ENDSTATEMENT = true
        WRITEHEADER = true
    }
    OUT.append("(")
    COLUMNS.eachWithIndex { column, idx ->
        def value = dataRow.value(column)
        def skipQuote = value.toString().isNumber() || value == null
        def stringValue = value != null ? FORMATTER.format(dataRow, column) : KW_NULL
        if (DIALECT.getDbms().isMysql()) stringValue = stringValue.replace("\\", "\\\\")
        OUT.append(skipQuote ? "": QUOTE).append(stringValue.replace(QUOTE, QUOTE + QUOTE))
           .append(skipQuote ? "": QUOTE).append(idx != COLUMNS.size() - 1 ? SEP : "")
    }
    if(!ENDSTATEMENT)
        ENDSTATEMENT =  rIdx == ROWSCOL.size() - 1
    OUT.append(ENDSTATEMENT ? ");" : "),").append(NEWLINE)
    if(WRITEHEADER) {
        OUT.append(KW_INSERT_INTO)
        if (TABLE == null) OUT.append("MY_TABLE")
        else OUT.append(TABLE.getParent().getName()).append(".").append(TABLE.getName())

        OUT.append(" (")

        COLUMNS.eachWithIndex { column, idx ->
            OUT.append(column.name()).append(idx != COLUMNS.size() - 1 ? SEP : "")
        }

        OUT.append(KW_VALUES).append(NEWLINE)
    }
}