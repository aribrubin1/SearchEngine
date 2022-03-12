package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;

//This page object handles instances of a single page. It stores the URL, lists of fruits, links and vectors. It
//Also contains the pagerank and the scores. The master list that the program runs off (pageDex) is a list of pages.

public class Page implements SearchResult {
    private String AbsURL;
    public String LocURL;
    private String MasterURL;
    private String Contents;
    private String[] Fruits;
    private ArrayList<String> StringLinks;
    private ArrayList<String> FullLinks;
    private double pageRank;
    public ArrayList<Double> pageVector;
    private double score;
    SearchEngine engine;
    public Page(String baseURL,String MasURL,SearchEngine instengine) throws IOException {
        engine = instengine;
        AbsURL = baseURL;
        MasterURL = MasURL;
        Contents = readContents(AbsURL);
        Fruits = ExtractFruit();
        StringLinks = ExtractLinks();
        FullLinks = FullStringExtractor();
        pageVector = new ArrayList<Double>();
    }

    public String getTitle(){
        int start = 0;
        int end = 0;
        for(int i = AbsURL.length()-1; i > 0;i--){
            if(AbsURL.charAt(i) == '.'){end = i;}
            if(AbsURL.charAt(i) == '/'){start = i;}
            if(start != 0 && end != 0){return AbsURL.substring(start+1,end);}
        }
        return AbsURL.substring(start+1,end);

    }


    //Just the provided readContents(). I found it easier to fold it in with the Page object.
    public static String readContents(String url) throws MalformedURLException, IOException {
        URL page = new URL(url);
        StringBuilder response = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(page.openStream()));
        String currentLine = reader.readLine();
        while(currentLine != null){
            response.append(currentLine + "\n");
            currentLine = reader.readLine();
        }
        return response.toString();
    }

    private String[] ExtractFruit() throws IOException {
        File fruitfile = new File("fruits/"+getTitle()+".txt");
        fruitfile.createNewFile();
        FileWriter mywrighter = new FileWriter(fruitfile);
        ArrayList<String> Fruitlist = new ArrayList<String>();
        int index = 2;
        int Start = 0;
        int End = 0;
        while (Start == 0){
            if(Contents.charAt(index)=='>'&&Contents.charAt(index-1)=='p'&&Contents.charAt(index-2)=='<'){Start=index+2;}
            index++;
        }
        while (End == 0){
            if(Contents.charAt(index)=='>'&&Contents.charAt(index-1)=='p'){End=index-4;}
            index++;
        }
        for(String s : Contents.substring(Start,End).split("\n")){mywrighter.write(s);mywrighter.write("\n");}
        mywrighter.close();
        return Contents.substring(Start,End).split("\n");
    }

    private ArrayList<String> ExtractLinks() throws IOException {
        File linkfile = new File("links/"+getTitle()+".txt");
        linkfile.createNewFile();
        FileWriter mywrighter = new FileWriter(linkfile);
        String[] contentlist = Contents.split("\n");
        ArrayList<String> links = new ArrayList<String>();
        for(String s : contentlist){
            if (s.charAt(0)=='<'&&s.charAt(1)=='a'){
                int start = 0;
                int end = 0;
                for(int i = 1;i<s.length()-1;i++){
                    if (s.charAt(i)=='.'&&s.charAt(i-1) == '"'){start=i;}
                    if (s.charAt(i)=='"' && s.charAt(i+1)=='>'){end=i;}
                }
                links.add(s.substring(start+2,end));
                mywrighter.write(s.substring(start+2,end) + "\n");
            }
        }
        mywrighter.close();
        return links;
    }

    private ArrayList<String> FullStringExtractor(){
        ArrayList<String> Fulls = new ArrayList<>();
        for(String URL : StringLinks){
            Fulls.add(MasterURL + URL);
        }
        return Fulls;
    }



    public String[] getFruits(){return Fruits;}
    public ArrayList<String> getLinks(){return StringLinks;}
    public String getAbsURL(){return AbsURL;}
    public ArrayList<String> getFullLinks(){return FullLinks;}
    public void setPageRank(double pageRank) {this.pageRank = pageRank;}
    public double getPageRank() {return pageRank;}
    @Override
    public double getScore() {return score;}
    public void setScore(double newscore){score=newscore;}
}
