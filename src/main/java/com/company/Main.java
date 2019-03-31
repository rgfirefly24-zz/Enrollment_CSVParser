package com.company;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, IndexOutOfBoundsException {
        /* Goal
            Import a csv file with User Id, First Name, Last Name, Version, Insurance Company
            Break said file into groups based on the following criteria
            1. Group the Contents of the File by the Insurance Company
            2. Next iterate over the contents and look for duplicate User Ids and if there are
               Then remove all records except for the highest Version number
            3. Then sort the remaining list by Last name then First Name
            4. Finally write each list out to it's own CSV using the Key (We'll use a dictionary to hold data) as
               apart of the file name

            So in theory this would have Try/Catches around the major items that could go wrong; however, since this
            is a controlled environment I'm not going to implement the try/catch.  Typically I would have some sort
            of logging framework such as the ELK stack or maybe just straight file logging which would capture the
            most common errors and log them.  The rule of Pokemon says that we should not have all encompassing
            Catch(Exception ex) since this catches all exceptions including runetime ones which we would typically
            want to let through as it could cause adverse effects in parent code.
            Also, Typically you'd want to break things down into logical methods such as GetHeaders, ProcessData
        */
        String testFile = "c:\\testFiles\\TestCSV.csv";
        CSVReader rdr = new CSVReader(new FileReader(testFile));
        HashMap<String, ArrayList<Enrollment>> companyDict;
        List<String> headers = new ArrayList<>(Arrays.asList(rdr.readNext()));
        companyDict = ProcessData(rdr, headers);

        for(Map.Entry<String,ArrayList<Enrollment>> entry: companyDict.entrySet()){
            CreateCSV(entry.getKey(), entry.getValue(), headers);
        }
    }

    private static HashMap<String, ArrayList<Enrollment>> ProcessData(CSVReader rdr, List<String> headers) throws IOException {
        String[] line;
        HashMap<String, ArrayList<Enrollment>> companyDict = new HashMap<>();
        while((line = rdr.readNext())!= null){
            Enrollment enrollment = new Enrollment();

            enrollment.setUserId(line[DetermineIndexByColumnName(headers,"UserId")]);
            enrollment.setFirstName(line[DetermineIndexByColumnName(headers,"FirstName")]);
            enrollment.setLastName(line[DetermineIndexByColumnName(headers,"LastName")]);
            if(TryParse(line[DetermineIndexByColumnName(headers,"Version")])) {
                Integer version = Integer.parseInt(line[DetermineIndexByColumnName(headers,"Version")]);
                enrollment.setVersion(version);
            }
            enrollment.setInsuranceCompany(line[DetermineIndexByColumnName(headers,"InsuranceCompany")]);

            if(companyDict.get(enrollment.getInsuranceCompany()) == null && enrollment.getInsuranceCompany() != null){
                ArrayList<Enrollment> enrollments = new ArrayList<>();
                enrollments.add(enrollment);
                companyDict.put(enrollment.getInsuranceCompany(), enrollments);
            }
            else{
                List<Enrollment> enrollments = companyDict.get(enrollment.getInsuranceCompany());
                Iterator<Enrollment> iterator = enrollments.iterator();

                while(iterator.hasNext()){
                    Enrollment e = iterator.next();

                    if(e.compareTo(enrollment) < 0){
                        iterator.remove();
                        break;
                    }
                }
                enrollments.add(enrollment);
            }
        }

        return companyDict;
    }

    private static Boolean CreateCSV(String fileName, List<Enrollment> lines, List<String> headers) throws IOException {
        //Get the Data
        try (FileOutputStream fos = new FileOutputStream(String.format("%s%s.csv","c:\\testFiles\\",fileName));
             OutputStreamWriter osw = new OutputStreamWriter(fos,
                     StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            List<String[]> data = CreateCSVLinesFromEnrollmentList(lines, headers);
            writer.writeAll(data);
            return true;
        }
        catch(IOException ex){
            throw ex;
            //would normally do some sort of logging here.
        }
    }

    private static List<String[]> CreateCSVLinesFromEnrollmentList(List<Enrollment> data, List<String> headers){
        List<String[]> lines = new ArrayList<>();

        // adding header record
        Iterator headerIterator = headers.iterator();
        String[] headerLine = new String[headers.size()];

        for(int i = 0; i < headers.size(); i++){
            headerLine[i] = headers.get(i);
        }

        lines.add(headerLine);

        Iterator<Enrollment> enrollmentIterator = data.iterator();

        while(enrollmentIterator.hasNext()){
            Enrollment e = enrollmentIterator.next();
            lines.add(new String[]{e.getUserId(), e.getFirstName(), e.getLastName(), e.getVersion().toString(), e.getInsuranceCompany()});
        }

        return lines;
    }
    /// We use a TryParse like this because
    /// 1. Apparently Java doesn't have extension methods
    /// 2. It also doesn't have a tryparse method because there are no "Out" parameters
    public static Boolean TryParse(String s){
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }

    public static Integer DetermineIndexByColumnName(List<String> headers, String columnName){
        if(headers.indexOf(columnName) == -1)
            throw new IndexOutOfBoundsException("Column does not Exist in Excel File");
        else
            return headers.indexOf(columnName);
    }
}
