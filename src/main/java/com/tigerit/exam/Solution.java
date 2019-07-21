package com.tigerit.exam;


import static com.tigerit.exam.IO.*;

import java.util.*;


/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
public class Solution implements Runnable {
    @Override
    public void run() {
        // your application entry point
        solve();

    }

    public static void solve()
    {
        Integer testCase = readLineAsInteger();

        for(int test=0; test<testCase; test++) {
            Integer totalTable = readLineAsInteger();
            System.out.println("Test: " + Integer.toString(test+1));
            ArrayList<Table> tables = new ArrayList<Table>();
            HashMap<String, Integer> mapTable = new HashMap<String, Integer>();

            for (int table = 0; table < totalTable; table++) {
                String tableName = readLine();
                mapTable.put(tableName, table);

                //totalCol, row
                String temp = readLine();
                Integer totalCol = Integer.parseInt(extractor(temp).get(0));
                Integer totalRow = Integer.parseInt(extractor(temp).get(1));

                //first row is the field row
                HashMap<String, Integer> mapField = new HashMap<String, Integer>();
                temp = readLine();
                ArrayList<String> extractedField = extractor(temp);
                for(int col=0; col<totalCol; col++){
                    mapField.put(extractedField.get(col), col);
                }


                //content in row column format (2d Matrix)
                ArrayList<ArrayList<Integer>> tableContent = new ArrayList<ArrayList<Integer>>();
                for(int row = 0; row < totalRow; row++)  {
                    tableContent.add(new ArrayList<Integer>()); //init all row
                }
                for(int row=0; row < totalRow; row++) {
                    temp = readLine();
                    ArrayList<String> rowContent = extractor(temp); //row
                    for (String eachCol : rowContent) {
                        tableContent.get(row).add(Integer.parseInt(eachCol)); //totalCol
                    }
                }
                Table t = new Table(tableName,
                        totalCol,
                        totalRow,
                        extractedField,
                        tableContent,
                        mapField );

                //t.showTable();
                tables.add(t);
            }


            //query
            Integer totalQuery = readLineAsInteger();
            for(int query=0; query<totalQuery; query++){
                String select = readLine();
                String from = readLine();
                String join = readLine();
                String on = readLine();

                String fake = readLine();



                if(extractor(from).size() == 2){
                    //q1
                    String str_table_one = on.split("\\s")[1];
                    String str_table_two = on.split("\\s")[3];

                    //figure out table 1
                    PairTableDotColumn t1 = extractTableDotCol(str_table_one, tables, mapTable );
                    Table table_one = tables.get(t1.tableIdx);

                    //figure out table 2
                    PairTableDotColumn t2 = extractTableDotCol(str_table_two, tables, mapTable);
                    Table table_two = tables.get(t2.tableIdx);

                    //joiner utility
                    Query q = joinUtility(table_one, table_two, t1.colIdx, t2.colIdx);
                    ArrayList<PairTableDotColumn> printable = selectAllPrintable(table_one, table_two, mapTable);
                    formatter(table_one,  table_two,  q, printable );
                }
                else{
                    if(extractor(select).get(1).equals("*")){
                        //q2
                        String alias_table_one = from.split("\\s")[2]; //catch the aliased name of table
                        String alias_table_two = join.split("\\s")[2];
                        HashMap<String, String> mapAlias = new HashMap<String, String>();//{ alias: original}
                        mapAlias.put(from.split("\\s")[2], from.split("\\s")[1]); //{ta: table_a}
                        mapAlias.put(join.split("\\s")[2], join.split("\\s")[1]);

                        String str_table_one = aliastToOriginal(on.split("\\s")[1], mapAlias);
                        String str_table_two = aliastToOriginal(on.split("\\s")[3], mapAlias);

                        //figure out table 1
                        PairTableDotColumn t1 = extractTableDotCol(str_table_one, tables, mapTable );
                        Table table_one = tables.get(t1.tableIdx);

                        //figure out table 2
                        PairTableDotColumn t2 = extractTableDotCol(str_table_two, tables, mapTable);
                        Table table_two = tables.get(t2.tableIdx);

                        //joiner utility
                        Query q = joinUtility(table_one, table_two, t1.colIdx, t2.colIdx);
                        ArrayList<PairTableDotColumn> printable = selectAllPrintable(table_one, table_two, mapTable);
                        formatter(table_one,  table_two,  q, printable );

                    }
                    else{
                        //q3
                        String alias_table_one = from.split("\\s")[2]; //catch the aliased name of table
                        String alias_table_two = join.split("\\s")[2];
                        HashMap<String, String> mapAlias = new HashMap<String, String>();//{ alias: original}
                        mapAlias.put(from.split("\\s")[2], from.split("\\s")[1]); //{ta: table_a}
                        mapAlias.put(join.split("\\s")[2], join.split("\\s")[1]);

                        String str_table_one = aliastToOriginal(on.split("\\s")[1], mapAlias);
                        String str_table_two = aliastToOriginal(on.split("\\s")[3], mapAlias);

                        //figure out table 1
                        PairTableDotColumn t1 = extractTableDotCol(str_table_one, tables, mapTable );
                        Table table_one = tables.get(t1.tableIdx);

                        //figure out table 2
                        PairTableDotColumn t2 = extractTableDotCol(str_table_two, tables, mapTable);
                        Table table_two = tables.get(t2.tableIdx);

                        //joiner utility
                        Query q = joinUtility(table_one, table_two, t1.colIdx, t2.colIdx);
                        ArrayList<PairTableDotColumn> printable = createPrintableFromString(select, table_one, table_two, tables, mapTable, mapAlias);
                        formatter(table_one,  table_two,  q, printable );

                    }
                }

                System.out.println("");

            }
        }

    }

    static String aliastToOriginal(String str, HashMap<String, String> mapAlias)
    {
        return  mapAlias.get(str.split("\\.")[0]) + "." + str.split("\\.")[1];
    }

    static ArrayList<PairTableDotColumn> createPrintableFromString(String str, Table t1, Table t2, ArrayList<Table> tables, HashMap <String, Integer> mapTable, HashMap <String, String> mapAlias)
    {
        ArrayList<PairTableDotColumn> printable = new ArrayList<PairTableDotColumn>();
        String[] splitted = str.split("\\s|,");
        for(int i=1; i<splitted.length; i++){ //started from 1 because we don't need "SELECT" token
            if(!splitted[i].equals("")){ //skip space
                String originalTable = aliastToOriginal( splitted[i], mapAlias);
                printable.add(extractTableDotCol(originalTable, tables, mapTable));
            }
        }

        return printable;

    }

    static ArrayList<PairTableDotColumn> selectAllPrintable(Table t1, Table t2, HashMap <String, Integer> mapTable)
    {
        ArrayList<PairTableDotColumn> printable = new ArrayList<PairTableDotColumn>();
        for(int i=0; i<t1.totalCol; i++){
            printable.add(new PairTableDotColumn(t1.tableName, t1.field.get(i), mapTable.get(t1.tableName), i));
        }
        for(int i=0; i<t2.totalCol; i++){
            printable.add(new PairTableDotColumn(t2.tableName, t2.field.get(i), mapTable.get(t2.tableName), i));
        }

        return printable;
    }

    static PairTableDotColumn extractTableDotCol(String str, ArrayList<Table> tables, HashMap<String, Integer> mapTable)
    {
        String tableName = str.split("\\.")[0];
        Integer tableIdx = mapTable.get(tableName);
        String colName = str.split("\\.")[1];
        Integer colIdx = tables.get(tableIdx).mapField.get(colName);
        return new PairTableDotColumn(tableName, colName, tableIdx, colIdx);
    }

    public static ArrayList<String> extractor(String str)
    {
        ArrayList <String> wordList = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(str, " ");
        while (st.hasMoreTokens()) {
            wordList.add(st.nextToken());
        }
        return wordList;
    }

    static Query joinUtility(Table t1, Table t2, int c1, int c2)
    {
        Query q = new Query();
        for(int i=0; i<t2.totalRow; i++){
            int cmp1 = t2.getCell(i, c1);
            for(int j=0; j<t1.totalRow; j++){
                int cmp2 = t1.getCell(j, c2);
                if(cmp1 == cmp2){
                    q.rowOfTable_one.add(j);
                    q.rowOfTable_two.add(i);
                }
            }
        }

        return q;
    }

    static void formatter(Table table_one, Table table_two, Query q, ArrayList<PairTableDotColumn> printable )
    {
        int limit = q.rowOfTable_one.size();
        for(int i=0; i<printable.size(); i++){
            System.out.printf(printable.get(i).colName );
            if(i+1 < printable.size()){
                System.out.print(" ");
            }
        }
        System.out.println("");

        ArrayList<ArrayList<Integer>> outputTable = new ArrayList<ArrayList<Integer>>();

        //retreve and sort
        for(int i=0; i<limit; i++){
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for(int j=0; j<printable.size(); j++){
                if(printable.get(j).tableName.equals(table_one.tableName)){
                    //t1
                    temp.add(table_one.getCell(q.rowOfTable_one.get(i), printable.get(j).colIdx));
                }
                else{
                    //t2
                    temp.add(table_two.getCell(q.rowOfTable_two.get(i), printable.get(j).colIdx));
                }

            }
            outputTable.add(temp);
        }

        Collections.sort(outputTable, new Comparator<ArrayList<Integer>>() {
            @Override
            public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
                for(int i=0; i<o1.size(); i++){
                    if(o1.get(i).equals(o2.get(i))){
                        continue;
                    }
                    else if(o1.get(i) < o2.get(i)){
                        return -1;
                    }
                    else if(o1.get(i) > o2.get(i)) {
                        return 1;
                    }
                }
                return 0;
            }
        });

        //output
        for(int i=0; i<outputTable.size(); i++){
            for(int j=0; j<outputTable.get(i).size(); j++){
                System.out.print(outputTable.get(i).get(j));
                if(j+1 < printable.size()){
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
    }

}


class Table{
    String tableName;
    Integer totalCol;
    Integer totalRow;
    ArrayList<String> field;
    ArrayList<ArrayList<Integer> > tableContent;
    HashMap <String, Integer> mapField;

    public Table(String tableName,
                 Integer totalCol,
                 Integer totalRow,
                 ArrayList<String> field,
                 ArrayList<ArrayList<Integer>> tableContent,
                 HashMap <String, Integer> mapField) {
        this.tableName = tableName;
        this.totalCol = totalCol;
        this.totalRow = totalRow;
        this.field = field;
        this.tableContent = tableContent;
        this.mapField = mapField;
    }

    public Integer getCell(int row, int col)
    {
        return tableContent.get(row).get(col);
    }
}


class PairTableDotColumn
{
    Integer tableIdx;
    Integer colIdx;

    String tableName;
    String colName;

    public PairTableDotColumn(String tableName, String colName, Integer tableIdx, Integer colIdx) {
        this.tableIdx = tableIdx;
        this.colIdx = colIdx;
        this.tableName = tableName;
        this.colName = colName;
    }

}

class Query{
    ArrayList <Integer> rowOfTable_one = new ArrayList<Integer>();
    ArrayList <Integer> rowOfTable_two = new ArrayList<Integer>();
}