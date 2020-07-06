/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spammail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author DELL
 */
public class SpamMail {

      // mảng chứa các túi từ của thư thường (non-spam)
  static ArrayList<Set<String>> listBagOfNonSpam = new ArrayList<>();
  // mảng chứa các túi từ của thư rác (spam)
  static ArrayList<Set<String>> listBagOfSpam = new ArrayList<>();
  // tinh xac xuat P(xi=x|nhan= nonspam)
  public static double pNonSpam(String x) { 
    double k = 0;
    for (int i = 0; i < listBagOfNonSpam.size(); i++) {
      // moi lan x xuat hien trong 1 thu thuong thi k++
      if (listBagOfNonSpam.get(i).contains(x))
        k++;
    }
    return Math.log((k + 1) / (listBagOfNonSpam.size() + 1));
    // P(xi|nhan= nonspam)= (k+1)/(sothuthuong+1);
    // trong do: k la so cac mail nonspam xuat hien xi
    // sothuthuong la so mail nonspam
  }
  // tinh xac xuat P(xi=x|nhan= spam)
  public static double pSpam(String x) {
    double k = 0;
    for (int i = 0; i < listBagOfSpam.size(); i++) {
      if (listBagOfSpam.get(i).contains(x))
        // moi lan x xuat hien trong 1 thu rac thi k++
        k++;
    }
    return Math.log((k + 1) / (listBagOfSpam.size() + 1));
    // P(xi|nhan= spam)= (k+1)/(sothurac+1);
    // trong do: k la so cac mail spam xuat hien xi
    // sothurac la so mail spam
  }
  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
 // đọc dữ liệu huấn luyện từ trước ở trong file result_training.dat ra
    System.out.println("Bắt đầu load dữ liệu huấn luyện");
    ObjectInputStream inp = new ObjectInputStream(
        new FileInputStream(new File("data/training/training_rs.dat")));
    listBagOfSpam = (ArrayList<Set<String>>) inp.readObject();
    listBagOfNonSpam = (ArrayList<Set<String>>) inp.readObject();
    inp.close();
    System.out.println("Hoàn load dữ liệu huấn luyện");
    // đọc dữ liệu từ mail cần kiểm tra
    System.out.println("Đọc dữ liệu mail cần kiểm tra");
    File mailTesting = new File("data/test/test2.txt");
    // Tiền xử lý mail cần kiểm tra
    String mailData = FileUtils.readFileToString(mailTesting, "UTF-8");
    Set<String> bagOfTest = RunTrainingData.toBagOfWord(mailData);
    System.out.println("Bắt đầu kiểm tra:");
    // xác xuất là thư thường. P(xi|non-spam)
    double C_NB1 = Math.log(listBagOfNonSpam.size() / ((double) listBagOfNonSpam.size() + listBagOfSpam.size()));
    // xác xuất là thư rác. P(xi|spam)
    double C_NB2 = Math.log(listBagOfSpam.size() / ((double) listBagOfNonSpam.size() + listBagOfSpam.size()));
    ArrayList<String> listStringTest = new ArrayList<>(bagOfTest);
    for (String strTest : listStringTest) {
      if (pNonSpam(strTest) != Math.log((double) 1 / (listBagOfNonSpam.size() + 1))
          || pSpam(strTest) != Math.log((double) 1 / (listBagOfSpam.size() + 1))) {
        System.out.println("P(x_i=" + strTest + "|nonspam)=  " + pNonSpam(strTest) + "        " + "P(x_i="
            + strTest + "|spam)=  " + pSpam(strTest));
        C_NB1 += pNonSpam(strTest);
        C_NB2 += pSpam(strTest);
      }
    }
    C_NB1 = Math.pow(Math.E, C_NB1);
    C_NB2 = Math.pow(Math.E, C_NB2);
    if (C_NB1 < C_NB2) {
      // Bổ sung thư vừa kiểm tra vào tập huấn luyện.
      listBagOfSpam.add(bagOfTest);
      System.out.println("Là thư rác");
    } else {
      listBagOfNonSpam.add(bagOfTest);
      System.out.println("Là thư thường");
    }
    // Lưu lại tập huấn luyện mới.
    ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream(new File("data/training/training_rs.dat")));
    out.writeObject(listBagOfSpam);
    out.writeObject(listBagOfNonSpam);
    out.close();
    System.out.println("Kết thúc");
  }

    
}
