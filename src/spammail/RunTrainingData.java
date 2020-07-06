/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spammail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import static spammail.SpamMail.listBagOfNonSpam;
import static spammail.SpamMail.listBagOfSpam;

/**
 *
 * @author DELL
 */
class RunTrainingData {

    public static Set<String> toBagOfWord(String mailData) {
        Set<String> rs = new HashSet();
        String [] listWord = mailData.split(" ");
        for(String str: listWord){
            rs.add(str);
        }
        return rs;
    }
    
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        Set<String> temp = toBagOfWord("Hello my friend");
//        for(String str: temp){
//            System.out.println(str);
//        }

        
        ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream(new File("data/training/training_rs.dat")));
        
        ArrayList<Set<String>> spam = new ArrayList<>();
        ArrayList<Set<String>> nonspam = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            File spamTesting = new File("data/spam/spam" + i +".txt");
            // Tiền xử lý mail cần kiểm tra
            String spamData = FileUtils.readFileToString(spamTesting, "UTF-8");
            Set<String> temp = toBagOfWord(spamData);
            spam.add(temp);
            
            File nonspamTesting = new File("data/nonspam/nonspam" + i +".txt");
            // Tiền xử lý mail cần kiểm tra
            String nonspamData = FileUtils.readFileToString(nonspamTesting, "UTF-8");
            Set<String> temp2 = toBagOfWord(nonspamData);
            nonspam.add(temp2);
        }
        out.writeObject(spam);
        out.writeObject(nonspam);
        out.close();
        ObjectInputStream inp = new ObjectInputStream(
        new FileInputStream(new File("data/training/training_rs.dat")));
        listBagOfSpam = (ArrayList<Set<String>>) inp.readObject();
        listBagOfNonSpam = (ArrayList<Set<String>>) inp.readObject();
        System.out.println("SPAM================================================================");
        for(Set<String> temp: listBagOfSpam){
            for(String str: temp){
                System.out.println(str);
            }
        }
        System.out.println("NONSPAM-============================================================");
        for(Set<String> temp: listBagOfNonSpam){
            for(String str: temp){
                System.out.println(str);
            }
        }
    }
}
