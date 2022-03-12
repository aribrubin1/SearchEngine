package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tester implements ProjectTester{
    SearchEngine engine;
    @Override
    public void initialize() throws IOException {
        engine = new SearchEngine();
        engine.filesetup();
    }

    @Override
    public void crawl(String seedURL) throws IOException {
        System.out.println("crawling #" + engine.pageDex.size() + " " + seedURL);
        String MasterURL = engine.getMasterURL(seedURL);
        Page curPage = new Page(seedURL,MasterURL,engine);
        engine.pageDex.add(curPage);
        engine.linkDex.add(curPage.getAbsURL());
        engine.urlPageMapper.put(seedURL,curPage);
        for(String URL : curPage.getLinks()){
            if(!engine.linkDex.contains(MasterURL + URL)){crawl(MasterURL + URL);}
        }


    }

    @Override
    public List<String> getOutgoingLinks(String url) throws IOException {
        Page temp = new Page(url,engine.MasterURL,engine);
        return temp.getLinks();
    }

    @Override
    public List<String> getIncomingLinks(String URL){
        List<String> listing = new ArrayList<>();
        for(Page p: engine.pageDex){
            if(!p.getAbsURL().equals(URL)){
                if(p.getLinks().contains(URL)){listing.add(p.getAbsURL());}
            }
        }
        if(listing.size()==0){return null;}
        return listing;
    }

    @Override
    public double getPageRank(String url){
        for(Page p : engine.pageDex){
            if(p.getAbsURL().equals(url)){return p.getPageRank();}
        }
        return 0.0;
    }

    @Override
    public double getIDF(String word){if(engine.idfdict.contains(word)){return engine.idfdict.get(word);}else{return 0;}}

    @Override
    public double getTF(String url, String word) throws IOException {
        double count = 0.0;
        double total = 0.0;
        for(String fruit : engine.urlPageMapper.get(url).getFruits()){
            if (word.equals(fruit)){count+=1.0;}
            total+=1.0;
        }
        return(count/total);
    }

    @Override
    public double getTFIDF(String url, String word) throws IOException {
        if(!engine.idfdict.containsKey(word)){return 0.0;}
        return ((Math.log(1+getTF(url,word))/Math.log(2))*engine.idfdict.get(word));
    }

    @Override
    public List<SearchResult> search(String phrase,boolean boost,int x) throws IOException {

        if(engine.idfdict.isEmpty()){engine.setIdfdict();}
        if(engine.matrix.isEmpty()){engine.matrix = engine.matrix();}
        if(engine.pageRankings.length==0){engine.pageRankings = engine.get_page_ranks();}

        HashMap<Page,Double> rankings = new HashMap<Page,Double>();
        String[] INITsearchwords = phrase.split(" ");
        ArrayList<String> searchwords = new ArrayList<>();
        for(String s : INITsearchwords){
            if(!searchwords.contains(s)){searchwords.add(s);}
        }
        ArrayList<Double> querieVector = new ArrayList<>();
        for(String i: searchwords){
            if(!engine.idfdict.containsKey(i)){querieVector.add(0.0);}
            else{querieVector.add(engine.idfdict.get(i)*(Math.log(1+engine.customtf(i,INITsearchwords))/Math.log(2)));}
        }

        ArrayList<Double> ranks = new ArrayList<>();
        for(Page p : engine.pageDex){
            ArrayList<Double> temp = new ArrayList<>();
            for(String word : searchwords){
                p.pageVector.add(getTFIDF(p.getAbsURL(),word));
            }
            double numerator = 0.0;
            double denominator1 = 0.0;
            double denominator2 = 0.0;
            for(int i=0;i<searchwords.size();i++){
                numerator += querieVector.get(i)*p.pageVector.get(i);
                denominator1 += Math.pow(querieVector.get(i),2);
                denominator2 += Math.pow(p.pageVector.get(i),2);
            }
            denominator1=Math.sqrt(denominator1);
            denominator2=Math.sqrt(denominator2);
            if(boost==true) {
                if (denominator1 != 0 && denominator2 != 0) {
                    ranks.add((numerator / (denominator1 * denominator2)) * p.getPageRank());
                }
                else {ranks.add(0.0);}}
            else{
                if(denominator1!=0 && denominator2!=0){ranks.add(numerator/(denominator1*denominator2));}
                else{ranks.add(0.0);}
            }
        }


        ArrayList<Page> copylist = new ArrayList<>();
        copylist = (ArrayList<Page>) engine.pageDex.clone();
        int n = ranks.size();
        for (int i = 0; i < n-1; i++) {
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (ranks.get(j) > ranks.get(min_idx))
                    min_idx = j;
            double temp = ranks.get(min_idx);
            double temp2 = ranks.get(i);
            Page temp3 = copylist.get(min_idx);
            Page temp4 =copylist.get(i);
            ranks.set(min_idx,temp2);
            ranks.set(i,temp);
            copylist.set(min_idx,temp4);
            copylist.set(i,temp3);
        }
        System.out.println("sorting into final listing");
        for(int q = 0; q < engine.pageDex.size(); q++){copylist.get(q).setScore(ranks.get(q));}
        List<SearchResult> finallisting = new ArrayList<>();
        for(int i = 0; i<x; i++){
            ArrayList<Page> finalCopylist = copylist;
            int finalI = i;
            finallisting.add(new SearchResult() {
                @Override
                public String getTitle() {
                    return finalCopylist.get(finalI).getTitle();
                }

                @Override
                public double getScore() {
                    return finalCopylist.get(finalI).getScore();
                }
            });

        }
        return finallisting;
    }
}
