package com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.io.File;
import java.util.List;

//This object is the master object for the entire program. Only one searchEngine object will exist. When you boot up
//the program and give it a link, it will create a SearchEngine object.

public class SearchEngine{
    String MasterURL;
    ArrayList<Page> pageDex;
    ArrayList<String> linkDex;
    ArrayList<ArrayList<Double>> matrix;
    public HashMap<String,Page> urlPageMapper;
    double[] pageRankings;
    public Hashtable<String, Double> idfdict;

    public SearchEngine() throws IOException {
        idfdict = new Hashtable<String,Double>();
        pageDex = new ArrayList<Page>();
        linkDex = new ArrayList<String>();
        matrix = new ArrayList<ArrayList<Double>>();
        pageRankings = new double[0];
        urlPageMapper = new HashMap<>();
    }


    public void filesetup() throws IOException {
        if(Files.exists(Paths.get("fruits"))){
            for(File f : Paths.get("fruits").toFile().listFiles()){f.delete();}
        }
        if(Files.exists(Paths.get("links"))){
            for(File f : Paths.get("links").toFile().listFiles()){f.delete();}
        }
        Path fruits = Paths.get("fruits");
        Path links = Paths.get("links");
        if(!Files.exists(fruits)){Files.createDirectory(fruits);}
        if(!Files.exists(links)){Files.createDirectories(links);}

    }

    public String getMasterURL(String SeedURL){
        int end = 0;
        for(int i = SeedURL.length()-1; i > 0;i--){
            if(SeedURL.charAt(i) == 'N'){end = i;}
        }
        return(SeedURL.substring(0,end));
    }

    public ArrayList<ArrayList<Double>> matrix() {
        double alpha = 0.1;
        ArrayList<ArrayList<Double>> matrice = new ArrayList<>();
        for (int i = 0; i < linkDex.size(); i++) {
            ArrayList<Double> templist = new ArrayList<>();
            ArrayList<Integer> Tempslotnum = new ArrayList<>();
            int tempdenominator = 0;
            for (int g = 0; g < linkDex.size(); g++) {
                if (pageDex.get(i).getFullLinks().contains(linkDex.get(g))) {
                    Tempslotnum.add(g);
                    tempdenominator += 1;
                }
                templist.add(alpha * (1.0 / linkDex.size()));
            }
            if (tempdenominator == 0) {
                for (int q = 0; q < templist.size(); q++) {
                    templist.set(q, (double) (1.0 / templist.size()));
                }
            }
            for (Integer g : Tempslotnum) {
                templist.set(g, (double) 1.0 / tempdenominator);
                templist.set(g, templist.get(g) * (1.0 - alpha));
                templist.set(g, templist.get(g) + (alpha * (1.0 / linkDex.size())));
            }
            matrice.add(templist);
        }
        return matrice;
    }

    public double[] get_page_ranks(){
        double[] starter = new double[linkDex.size()];
        starter[0] = 1.0;
        return rank(starter);
    }

    private double[] rank(double[] starter){
        double[] copystarter = new double[starter.length];
        System.arraycopy(starter,0,copystarter,0,starter.length);
        for(int row = 0; row < linkDex.size(); row++){
            double tempnum = 0.0;
            for(int colomn = 0;colomn<linkDex.size();colomn++){
                tempnum+=matrix.get(colomn).get(row)*copystarter[colomn];
            }
            starter[row]=tempnum;
        }
        double distance = 0.0;
        for(int i = 0; i<copystarter.length;i++){
            distance+=Math.pow((starter[i]-copystarter[i]),2);
        }
        distance=Math.sqrt(distance);
        if(distance<=0.0001){
            for(int i = 0; i<starter.length;i++){pageDex.get(i).setPageRank(starter[i]);}
            return starter;
        }
        else{return rank(starter);}
    }


    public void setIdfdict(){
        HashMap<String,Integer> tempdict = new HashMap<String,Integer>();
        ArrayList<String> wordlist = new ArrayList<>();
        for(Page p : pageDex){
            ArrayList<String> unique = new ArrayList<>();
            for(String word : p.getFruits()){
                if (!unique.contains(word)){
                    unique.add(word);
                    wordlist.add(word);
                    if(tempdict.containsKey(word)){tempdict.put(word,tempdict.get(word)+1);}
                    else{tempdict.put(word,1);}
                }
            }
        }
        for(String word : wordlist){
            idfdict.put(word,Math.log((double)pageDex.size()/(1.0+(double)tempdict.get(word)))/Math.log(2));
        }
    }

    public double customtf(String word,String[] Fruits){
        double count = 0.0;
        double total = 0.0;
        for(String fruit : Fruits){
            if (word.equals(fruit)){count+=1.0;}
            total+=1.0;
        }
        return(count/total);
    }
}
