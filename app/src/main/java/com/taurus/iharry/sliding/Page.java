package com.taurus.iharry.sliding;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;

import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


import com.taurus.iharry.ChapterAndContent;
import com.taurus.iharry.R;
import com.taurus.iharry.database.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by taurus on 16/9/10.
 */
public class Page extends ContentView {

    private ArrayList<String> wholeTxt= null;
    private ArrayList<String> tempTxt = null;
    private String filename=null;
    private float textSize;
    private final float leading = 1.0f;
    private String chapter = null;
    private ArrayList<ChapterAndContent> chapters=null;

    public ArrayList<ChapterAndContent> getChapters() {
        return chapters;
    }

    public Page(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Page(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Page(Context context, String filename,float textSize) {
        super(context);
        this.textSize = textSize;
        this.wholeTxt = new ArrayList<String>();
        this.filename = filename;
        this.chapters= new ArrayList<ChapterAndContent>();
        loadText();
    }
    public Page(Context context, ArrayList<String> pages,float textSize) {
        super(context);
        this.tempTxt = new ArrayList<String>();
        for(String e:pages){
            tempTxt.add(e);
        }
        this.textSize = textSize;

    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public ArrayList<String> getWholeTxt() {
        return wholeTxt;
    }

    private void loadText(){
        InputStream input=null;
        Resources res = super.getResources();
        if(this.filename.equals("Harry1")){
         input  = res.openRawResource(R.raw.harry1);}
        else if(this.filename.equals("Harry2")){
            input = res.openRawResource(R.raw.harry2);
        }
        else if(this.filename.equals("Harry3")){
            input = res.openRawResource(R.raw.harry3);
        }
        else if(this.filename.equals("Harry4")){
            input = res.openRawResource(R.raw.harry4);
        }
        else if(this.filename.equals("Harry5")){
            input = res.openRawResource(R.raw.harry5);
        }
        else if(this.filename.equals("Harry6")){
            input = res.openRawResource(R.raw.harry6);
        }
        else if(this.filename.equals("Harry7")){
            input = res.openRawResource(R.raw.harry7);
        }
        Scanner scanner = new Scanner(input);
//        File file = new File(getContext().getFilesDir(),this.filename);
//        FileReader fis = null;
//        try {
//            fis = new FileReader(file);
//            BufferedReader br = new BufferedReader(input);
            String line = null;
//            while ((line = br.readLine()) != null) {
//                if(line.length()!=0){
//                    this.wholeTxt.add(line+" ");}
//                else{
//                    this.wholeTxt.add("\n");
////                    this.wholeTxt.add("\n");
//                }
//
//            }
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if(line.length()!=0){
                    this.wholeTxt.add(line+" ");}
                else{
                    this.wholeTxt.add("TURN");
//                    this.wholeTxt.add("\n");
                }

            }
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private int getEstimatedLines(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        TextPaint textPaint  = new TextPaint();
        textPaint.setTextSize(this.textSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float wordTop = fontMetrics.ascent;
        float wordBottom = fontMetrics.descent;
        float wordHeight = wordBottom- wordTop;
        int linenum  = (int) (displayMetrics.heightPixels/(wordHeight+this.leading));
        return linenum;
    }
//    private int getEstimatedLineWords(){
//        TextPaint textPaint  = new TextPaint();
//        textPaint.setTextSize(this.textSize);
//        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//        int linewords = (int)(this.width/ textSize);
//        return linewords;
//    }


    public ArrayList<String> seperateTextforParagraph(){
        String eachParagraph=" ";
        ArrayList<String> paragraphs = new ArrayList<String>();
        for(String e:wholeTxt){
            if(!e.equals("TURN")){
                eachParagraph+=e;
            }else{
                paragraphs.add(eachParagraph);
                eachParagraph=" ";
            }
        }
        for(int i = 0;i<paragraphs.size();i++){
//            String [] words = paragraphs.get(i).split(" ");
//            eachParagraph = " ";
//            for(String e:words){
//                eachParagraph+=e+" ";
//            }
            eachParagraph = paragraphs.get(i).trim();
            paragraphs.set(i,eachParagraph);

        }
        for(int i=0;i<paragraphs.size();i++){
            String temp = "      "+paragraphs.get(i);
            paragraphs.set(i,temp);
        }

        return paragraphs;
    }
    public ArrayList<String> seperateParagraphForLine2(){
        StaticLayout staticLayout = null;
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(this.textSize);
        String line  = "";
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> paragraphs = seperateTextforParagraph();
        DisplayMetrics displayMetrics  = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
//        int screenWidth = displayMetrics.widthPixels;

        for(String e:paragraphs){
            staticLayout = new StaticLayout(e,textPaint,screenWidth,Layout.Alignment.ALIGN_NORMAL,1f,0f,true);
            int lineCount = staticLayout.getLineCount();
            for(int i=0;i<lineCount;i++){
                int lineStartChar = staticLayout.getLineStart(i);
                int lineEndChar = staticLayout.getLineEnd(i);
                line = e.substring(lineStartChar,lineEndChar);
//                lines.add(line);  原来的
                if(i==lineCount-1){
                    lines.add(line+"\n");
                }
                else{
                lines.add(line);}

            }
            lines.add("SEPERATE");
        }


        return lines;

    }


public ArrayList<String> changeFontForPage(){
    StaticLayout staticLayout = null;
    ArrayList<String> pages = new ArrayList<String>();
    ArrayList<String> para = new ArrayList<String>(); //存储拿到的页的每一段
    TextPaint textPaint  = new TextPaint();
    textPaint.setTextSize(this.textSize);
    int num1 =0;
    int num2=0;
    for(int i=0;i<tempTxt.size();i++) {
        boolean state = false;

        if (tempTxt.get(i).length() != 0){
            if(tempTxt.get(i).substring(tempTxt.get(i).length() - 1).equals("\n")){
                state = true;
            }
            String tempTest  = tempTxt.get(i).trim();
            String[] eachPage1 = tempTest.split("\n\n");//得到现有页的每段
        if (state) {   //说明这个段最后刚好就是这个段结束
            for (String e : eachPage1) {
                para.add(e);
            }
        } else {   //说明本页的最后的那段并没完
            num1++;
            for (String e : eachPage1) {
                para.add(e);
            }
            para.add("CONTINUE");
        }
    }
    }
    for(int i=0;i<para.size();i++){
        if(para.get(i).equals("CONTINUE")){
            num2++;
            if(i!=para.size()-1){
            String a = para.get(i-1).trim();
            String b = para.get(i+1).trim();
//            String pa  = a+b;
                String pa  = a+" "+b;
            para.set(i-1,pa);
            para.remove(i);
            para.remove(i);}
        }
    }

    for(int i=0;i<para.size();i++){

        String temp = "      "+para.get(i).trim();
        para.set(i,temp);
    }
    Log.d("有几个段",String.valueOf(num1));
    Log.d("差了几次",String.valueOf(num2));
    String line  = "";
    ArrayList<String> lines = new ArrayList<String>();
    DisplayMetrics displayMetrics  = getResources().getDisplayMetrics();
    int screenWidth = displayMetrics.widthPixels;
    for(String e:para){
        staticLayout = new StaticLayout(e,textPaint,screenWidth,Layout.Alignment.ALIGN_NORMAL,1f,0f,true);
        int lineCount = staticLayout.getLineCount();
        for(int i=0;i<lineCount;i++){
            int lineStartChar = staticLayout.getLineStart(i);
            int lineEndChar = staticLayout.getLineEnd(i);
            line = e.substring(lineStartChar,lineEndChar);
//            lines.add(line);

            if(i==lineCount-1){
                lines.add(line+"\n");
            }
            else{
                lines.add(line);}
        }
        lines.add("SEPERATE");
    }
    int countLine =0;
    String eachPage = "";
    int linenum = lines.size();//总共多少行，包括seperate
    int lineForPage = getEstimatedLines()-3;//每一页多少行
    while (linenum>lineForPage){
        int count =0;
//        int numForDraw =0;
        eachPage += "\n";//有一行了
//        numForDraw++;
        if(chapter!=null){//分章节
            eachPage += chapter;
            chapter=null;
        }
        for(int i =countLine;i<countLine+lineForPage;i++){
            count++;
            if(!lines.get(i).contains("CHAPTER")){
                if(lines.get(i).equals("SEPERATE")){
                    eachPage+="\n";//再一行
                }
                else{
                    eachPage += lines.get(i);}
            }else if(lines.get(i).contains("CHAPTER ONE")){
                if(lines.get(i).equals("SEPERATE")){
                    eachPage+="\n";//再一行
                }
                else{
                    eachPage += lines.get(i);}
            }else{
                int rest =  lineForPage-count;
            for(int j=0;j<rest;j++){
                eachPage+="\n";
            }
            chapter=lines.get(i);
            break;
            }
//            if(lines.get(i).equals("SEPERATE")){
////                if(numForDraw<lineForPage){
//                eachPage+="\n";//再一行
////                eachPage+="\n";//再一行
////                    numForDraw=numForDraw+2;
////                     }
//            }
//            else{
//                eachPage += lines.get(i);}
        }
        pages.add(eachPage);
        eachPage="";
        countLine += count;
        linenum -=count;
    }
    for(int i=countLine;i<linenum;i++){
        eachPage += "\n";
        if(lines.get(i).equals("SEPERATE")){
            eachPage+="\n";
//            eachPage+="\n";
        }
        else{
            eachPage += lines.get(i);}
    }
    pages.add(eachPage);
    eachPage="";
    chapter=null;
    return pages;
}
    public ArrayList<String> seperateTextForPage(){
        ArrayList<String> pages = new ArrayList<String>();
        TextPaint textPaint  = new TextPaint();
        textPaint.setTextSize(this.textSize);
        ArrayList<String> lines = seperateParagraphForLine2();
//        StaticLayout staticLayout;
        int countLine =0;//在第几行了

        String eachPage = "";
        int linenum = lines.size();
        int lineForPage = getEstimatedLines()-3;
        while (linenum>lineForPage){
            int count =0;//一共用了多少行了
            eachPage += "\n";
            if(chapter!=null){
                eachPage += chapter;
                chapter=null;
            }
            for(int i =countLine;i<countLine+lineForPage;i++){
                count++;
                if(!lines.get(i).contains("CHAPTER")){

                if(lines.get(i).equals("SEPERATE")){
                        eachPage+="\n";//再一行
                }
                else{
                eachPage += lines.get(i);
                }
                }
                else if(lines.get(i).contains("CHAPTER ONE")){
                    ChapterAndContent temp = new ChapterAndContent(lines.get(i).trim(),lines.get(i+2).trim());//拿到目录和内容
                    chapters.add(temp);
                    if(lines.get(i).equals("SEPERATE")){
                        eachPage+="\n";//再一行
                    }
                    else{
                        eachPage += lines.get(i);
                    }

                }
                else{
                    ChapterAndContent temp = new ChapterAndContent(lines.get(i).trim(),lines.get(i+2).trim());
                    chapters.add(temp);
                   int rest =  lineForPage-count;
                    for(int j=0;j<rest;j++){
                        eachPage+="\n";
                    }
                    chapter=lines.get(i);
                    break;

                }
            }
            pages.add(eachPage);
            eachPage="";
            countLine += count;
            linenum -=count;
        }
        for(int i=countLine;i<linenum;i++){
            eachPage += "\n";
            if(lines.get(i).equals("SEPERATE")){
                eachPage+="\n";
            }
            else{
            eachPage += lines.get(i);}
        }
        pages.add(eachPage);
        eachPage="";
        chapter=null;
//        saveChapter(chapters);
//        if(this.filename.equals("Harry1")){//针对不同的书放不同的目录
//        saveChapter(chapters);}
//        for(){
//
//        }
          return pages;
    }

}


