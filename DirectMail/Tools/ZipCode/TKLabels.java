/*
 * TKLabels.java
 *
 * Created on 7 ÓåðôÝìâñéïò 2007, 10:59 ðì
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.ZipCode;

import soldatos.connection.MyDBCConnection;
import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import com.lowagie.text.*;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

/**
 *
 * @author ssoldatos
 */
public class TKLabels implements Runnable {

  Table t;
  PdfPTable pt;
  Document doc;
  PdfWriter writer;
  int sortMin;
  int packMax;
  boolean elta;
  boolean pressPost;
  boolean addBarcode;
  boolean infoPost;
  String center;
  String sDate;
  String eDate;
  String sDay;
  String eDay;
  private Statement stmt;
  private MyDBCConnection mdbc;
  private final int workingDays = 7;
  private String adv;
  /**
   * The {@link BufferedReader} that reads the {@link File}
   */
  BufferedReader in;
  /**
   * The {@link PrintWriter} that writes to the output {@link File}
   */
  PrintWriter output;
  private int fields;
  long start, end;
  private PdfContentByte cb;
  private MainForm m;

  /**
   * 
   * @param sortMin
   * @param packMax
   * @param addBarcode
   * @param elta
   * @param infoPost
   * @param pressPost
   * @param center
   * @param sDate
   * @param adv
   * @param m
   * @throws SQLException
   */
  public TKLabels(int sortMin, int packMax, boolean addBarcode, boolean elta,
      boolean infoPost, boolean pressPost, String center, String sDate, String adv, MainForm m) throws SQLException {
    this.m = m;
    this.sortMin = sortMin;
    this.packMax = packMax;
    this.elta = elta;
    this.pressPost = pressPost;
    this.center = center;
    this.addBarcode = addBarcode;
    this.infoPost = infoPost;
    this.sDate = sDate;
    this.adv = adv;


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);


    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);


    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();

  }

  @Override
  public void run() {
    try {
      createLabels();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }

  }

  private void createLabels() throws DocumentException, FileNotFoundException, IOException, SQLException {
    start = System.currentTimeMillis();
    // Make the connection
    m.setOutput("Connecting");
    if (elta) {
      doc = new Document(PageSize.A4.rotate(), 10, 10, 50, 30);
    } else {
      doc = new Document(PageSize.A4, 10, 10, 50, 30);
    }

    writer = PdfWriter.getInstance(doc, new FileOutputStream(m.getCurrentDirectory() + "//TKLabels.pdf"));
    doc.open();
    cb = writer.getDirectContent();

    m.appendOutput("\nCreating the label table");
    createTables();


    m.appendOutput("\nCreating the num of tk table");
    fillLabelsTable();


    m.appendOutput("\nGenerating the PDF");

    makePDF();
    doc.close();

    // Drop table
    dropTable();

    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private void computeDays() {
    int day = 0, month = 0, year = 0;
    int sDayInt, numberOfDays = 0;
    SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
    String sDateArr[];
    sDateArr = (sDate.split("/", -1));
    day = Integer.parseInt(sDateArr[0]);
    month = Integer.parseInt(sDateArr[1]) - 1;
    year = Integer.parseInt(sDateArr[2]);
    Calendar cal = new GregorianCalendar();
    cal.clear();
    cal.set(year, month, day);
    sDayInt = cal.get(Calendar.DAY_OF_WEEK);
    sDateArr = DateFormatSymbols.getInstance().getWeekdays();
    sDay = sDateArr[sDayInt].toUpperCase();


    while (numberOfDays < workingDays) {
      cal.add(Calendar.DAY_OF_MONTH, 1);
      if (cal.get(Calendar.DAY_OF_WEEK) != 1 && cal.get(Calendar.DAY_OF_WEEK) != 7) {
        numberOfDays++;
      }
    }
    eDay = sDateArr[cal.get(Calendar.DAY_OF_WEEK)].toUpperCase();
    eDate = s.format(cal.getTime());
  }

  private void createNewInfoPostPage(String tk_1, String tk_2) throws DocumentException, IOException {



    pt = makeInfoPostTable(tk_1);
    doc.add(pt);
    pt = makeInfoPostTable(tk_2);
    doc.add(pt);

    doc.newPage();

  }

  private void createNewEltaPage(String tk_1, String tk_2, String tk_3, String tk_4) throws DocumentException, IOException {
    BaseFont bf;
    Font normalFont;
    Font tkFont;

    t = new Table(2, 2);
    t.setBorderWidth(0);
    t.setAlignment("center");
    t.setPadding(40);

    cb.setLineWidth(1f);
    cb.moveTo(10, 302);
    cb.lineTo(30, 302);
    cb.stroke();

    cb.setLineWidth(1f);
    cb.moveTo(427, 600);
    cb.lineTo(427, 580);
    cb.stroke();

    bf = BaseFont.createFont(
        "C:/Windows/Fonts/times.ttf",
        BaseFont.IDENTITY_H,
        BaseFont.EMBEDDED);
    normalFont = new Font(bf, 26, Font.BOLD);
    tkFont = new Font(bf, 66, Font.BOLD);

    Phrase ph = new Phrase();
    ph.add(new Phrase("\n\n" + adv + "\n\n\n\n", normalFont));
    ph.add(new Phrase(tk_1, tkFont));
    Cell cell_1 = new Cell(ph);
    if (addBarcode) {
      cell_1.add(new Phrase(new Chunk(createBarcode(tk_1), 0, -45)));
    }
    cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);

    ph = new Phrase();
    ph.add(new Phrase("\n\n" + adv + "\n\n\n\n", normalFont));
    ph.add(new Phrase(tk_2, tkFont));
    Cell cell_2 = new Cell(ph);
    if (addBarcode) {
      cell_2.add(new Phrase(new Chunk(createBarcode(tk_2), 0, -45)));
    }
    cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);

    ph = new Phrase();
    ph.add(new Phrase("\n\n" + adv + "\n\n\n\n", normalFont));
    ph.add(new Phrase(tk_3, tkFont));
    Cell cell_3 = new Cell(ph);
    if (addBarcode) {
      cell_3.add(new Phrase(new Chunk(createBarcode(tk_3), 0, -45)));
    }
    cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);

    ph = new Phrase();
    ph.add(new Phrase("\n\n" + adv + "\n\n\n\n", normalFont));
    ph.add(new Phrase(tk_4, tkFont));
    Cell cell_4 = new Cell(ph);
    if (addBarcode) {
      cell_4.add(new Phrase(new Chunk(createBarcode(tk_4), 0, -45)));
    }
    cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);

    cell_1.setBorderWidth(0);
    cell_2.setBorderWidth(0);
    cell_3.setBorderWidth(0);
    cell_4.setBorderWidth(0);

    t.addCell(cell_1);
    t.addCell(cell_2);
    t.addCell(cell_3);
    t.addCell(cell_4);

    doc.add(t);




    doc.newPage();

  }

  private Image createBarcode(String tk_1) {
    PdfContentByte contentByte = writer.getDirectContent();
    Barcode128 code128 = new Barcode128();
    code128.setCode(tk_1);
    code128.setFont(null);
    code128.setBarHeight(40);
    code128.setX(2);
    Image image128 = code128.createImageWithBarcode(contentByte, null, null);

    return image128;
  }

  private void createTables() throws SQLException {
    String sql, sql4;

    sql = "CREATE TABLE IF NOT EXISTS  "+ DmOptions.TMP_DB +"." +
        MainForm.options.toString(DmOptions.DB_PREFIX) + "_tklabels" +
        "(col1 VARCHAR(45) default '     ')";

    sql4 = "TRUNCATE "+ DmOptions.TMP_DB +"."  + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tklabels";


    stmt.executeUpdate(sql);
    stmt.executeUpdate(sql4);

  }

  private void fillTable() {
    int j = 0;
    String str, tk = "", sql = "", insertStmt = "";
    String strArr[] = new String[fields];
    int lines = 0;

    try {
      while ((str = in.readLine()) != null) {
        lines++;
        m.appendToCurrentOutput("Lines read: " + lines);
        m.updateProgress(lines * 100 / m.getCustomers());
        if (lines % 50 == 0) {
          DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        }
        strArr = str.split("" + m.getDelimeter(), -1);
        insertStmt = "";
        for (int i = 0; i < strArr.length; i++) {
          strArr[i] = strArr[i].trim();
          if (i == m.getTkField()) {
            tk = DmFunctions.prepareTK(strArr[i]);
          }
        }
        sql = "INSERT INTO  " + DmOptions.TMP_DB +"."
            + MainForm.options.toString(DmOptions.DB_PREFIX)+ "_tk VALUES ('" + tk + "')";
        stmt.addBatch(sql);
      }
      m.updateProgress(0);
      in.close();
      m.appendOutput("\nDB is being updating");
      m.IndeterminateProgress(true);
      m.setProgressText("");

      stmt.executeBatch();

      m.IndeterminateProgress(false);
      m.updateProgress(100);

      in.close();

    } catch (IOException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    } catch (SQLException ex) {
      Errors.SQLError(ex.getMessage());
      ex.printStackTrace();
    }
  }

  private PdfPTable makeInfoPostTable(String tk) throws DocumentException, IOException {
    BaseFont bf;
    Font titleFont, normalFont, tkFont;
    float widths[] = {1, 7, 2};
    Chunk ch;
    Paragraph para;
    PdfPCell cell;
    Phrase ph;

    bf = BaseFont.createFont(
        "C:/Windows/Fonts/times.ttf",
        BaseFont.IDENTITY_H,
        BaseFont.EMBEDDED);
    titleFont = new Font(bf, 44, Font.BOLD);
    normalFont = new Font(bf, 10, Font.BOLD);
    tkFont = new Font(bf, 66, Font.BOLD);

    pt = new PdfPTable(3);
    pt.setWidths(widths);

    //Make cell INFOPOST /Colspan 3
    String title = infoPost ? "INFOPOST" : "PRESSPOST";
    para = new Paragraph(title, titleFont);
    cell = new PdfPCell(para);
    cell.setColspan(3);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(50);
    pt.addCell(cell);

    // Make Cell Stoixeia katathesis rowspan 3
    para = new Paragraph("ÓÔÏÉ×ÅÉÁ ÊÁÔÁÈÅÓÇÓ", normalFont);
    cell = new PdfPCell(para);
    cell.setRotation(90);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(99);
    pt.addCell(cell);

    // make 3 row cell / table
    PdfPTable pt2 = new PdfPTable(1);
    para = new Paragraph("ÊÁÔÁÈÅÓÇ ÓÔÏ ÊÅÍÔÑÏ ÄÉÁËÏÃÇÓ", normalFont);
    cell = new PdfPCell(para);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(33);
    pt2.addCell(cell);
    para = new Paragraph("ÇÌÅÑÁ ÊÁÔÁÈÅÓÇÓ", normalFont);
    cell = new PdfPCell(para);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(33);
    pt2.addCell(cell);
    para = new Paragraph("ÇÌÅÑÏÌÇÍÉÁ ÊÁÔÁÈÅÓÇÓ", normalFont);
    cell = new PdfPCell(para);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(33);
    pt2.addCell(cell);
    pt.addCell(pt2);

    // make 3 row cell / table
    PdfPTable pt3 = new PdfPTable(1);
    para = new Paragraph(center, normalFont);
    cell = new PdfPCell(para);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(33);
    pt3.addCell(cell);
    para = new Paragraph(sDay, normalFont);
    cell = new PdfPCell(para);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(33);
    pt3.addCell(cell);
    para = new Paragraph(sDate, normalFont);
    cell = new PdfPCell(para);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(33);
    pt3.addCell(cell);
    pt.addCell(pt3);

    // Add empty row
    para = new Paragraph(" ", normalFont);
    cell = new PdfPCell(para);
    cell.setColspan(3);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(20);
    pt.addCell(cell);

    //Make cell Epidosi  /Colspan 3
    para = new Paragraph("ÅÐÉÄÏÓÇ ÓÅ 7 ÅÑÃÁÓÉÌÅÓ ÌÅÑÅÓ ÁÐÏ ÔÇÍ ÇÌÅÑÁ ÊÁÔÁÈÅÓÇÓ ÓÔÏ " + center, normalFont);
    cell = new PdfPCell(para);
    cell.setColspan(3);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(30);
    pt.addCell(cell);

    // Make Cell Stoixeia Epidosis
    para = new Paragraph("ÓÔÏÉ×ÅÉÁ ÅÐÉÄÏÓÇÓ", normalFont);
    cell = new PdfPCell(para);
    cell.setRotation(90);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(140);
    pt.addCell(cell);

    // Make Cell Stoixeia Epidosis
    para = new Paragraph(tk, tkFont);
    if (addBarcode) {
      ch = new Chunk(createBarcode(tk), 0, 0);
      ph = new Phrase(ch);
      Paragraph tmpPara = new Paragraph("");
      para.add(tmpPara);
    }
    cell = new PdfPCell(para);

    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(140);
    pt.addCell(cell);

    // Make Empty cell
    para = new Paragraph("", normalFont);
    cell = new PdfPCell(para);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(140);
    pt.addCell(cell);

    //Make hmer Epidosis  /Colspan 3
    para = new Paragraph("ÇÌÅÑÏÌÇÍÉÁ ÅÐÉÄÏÓÇÓ ÌÅ×ÑÉ : " + eDate, normalFont);
    cell = new PdfPCell(para);
    cell.setColspan(3);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setFixedHeight(30);
    pt.addCell(cell);



    return pt;
  }

  private void makePDF() throws SQLException, DocumentException, IOException {
    ResultSet rs = null;

    String sql;
    int total = 0, i = 0;

    sql = "SELECT * FROM  " + DmOptions.TMP_DB +"."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tklabels ";

    rs = (ResultSet) stmt.executeQuery(sql);
    rs.last();
    total = rs.getRow();


    makeLabels(rs, total);
  }

  private void makeLabels(ResultSet rs, int total) throws SQLException, DocumentException, IOException {
    String label[] = new String[total + 4];
    String tk_1 = "", tk_2 = "", tk_3 = "", tk_4 = "", curTk;
    int i = 0;
    double pages = 0.0;

    rs.beforeFirst();
    while (rs.next()) {
      curTk = rs.getString(1);
      label[i] = curTk;
      i++;

      if (elta) {
        pages = total * 100 / 4;
        pages = Math.ceil(pages / 100);
        for (int j = 0; j < pages; j++) {
          tk_1 = label[j];
          tk_2 = label[j + (int) pages];
          tk_3 = label[j + 2 * (int) pages];
          tk_4 = label[j + 3 * (int) pages];

          if (tk_1 == null) {
            tk_1 = "     ";
          }
          if (tk_2 == null) {
            tk_2 = "     ";
          }
          if (tk_3 == null) {
            tk_3 = "     ";
          }
          if (tk_4 == null) {
            tk_4 = "     ";
          }
          createNewEltaPage(tk_1, tk_2, tk_3, tk_4);
        }
      } else {
        computeDays();
        pages = total * 100 / 2;
        pages = Math.ceil(pages / 100);
        for (int j = 0; j < pages; j++) {
          tk_1 = label[j];
          tk_2 = label[j + (int) pages];
          if (tk_1 == null) {
            tk_1 = "     ";
          }
          if (tk_2 == null) {
            tk_2 = "     ";
          }
          createNewInfoPostPage(tk_1, tk_2);
        }
      }


    }
  }

  private void dropTable() throws SQLException {
    String sql;

    sql = "DROP TABLE IF EXISTS  " + DmOptions.TMP_DB +"."  + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tklabels";
    //System.out.println(sql);

    stmt.executeUpdate(sql);

  }

  /**
   * Drops the temporary tables after the work is done
   */
  private void dropTmpTables() throws SQLException {
    String sql = "DROP TABLE IF EXISTS  "+ DmOptions.TMP_DB +"."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp";
    //System.out.println(sql);

    stmt.executeUpdate(sql);

    sql = "DROP TABLE IF EXISTS  "+ DmOptions.TMP_DB +"."
        +MainForm.options.toString(DmOptions.DB_PREFIX) + "_tk";
    //System.out.println(sql);

    stmt.executeUpdate(sql);

    sql = "DROP TABLE IF EXISTS  "+ DmOptions.TMP_DB +"."
        + MainForm.options.toString(DmOptions.DB_PREFIX)+ "_tmpNumoftk";

    stmt.executeUpdate(sql);

  }

  private void fillLabelsTable() throws IOException, SQLException {
    String str = "", curTk = null, prevTk = "dummy", sql = "";
    int lines = 0, tkCount = 1, inserts = 0;
    String strArr[];

    //read the file and import the tk

    while ((str = in.readLine()) != null) {
      lines++;

      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      strArr = str.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < strArr.length; i++) {
        strArr[i] = strArr[i].trim();
        if (i == m.getTkField()) {
          // delete the @
          curTk = strArr[i].replaceAll("@", "");
          curTk = DmFunctions.prepareTK(curTk);
          if (curTk.length() > 4) {
            curTk = curTk.substring(0, 5);
          } else {
            //curTk = "     ";
          }

          if (curTk.equals(prevTk)) {
            tkCount++;
          } else {
            if (tkCount >= sortMin) {
              inserts = (tkCount / packMax) + (tkCount % packMax > 0 ? 1 : 0);
              for (int j = 0; j < inserts; j++) {
                sql = "INSERT INTO  " + DmOptions.TMP_DB +"."
                    + MainForm.options.toString(DmOptions.DB_PREFIX) +
                    "_tklabels VALUES ('" + prevTk + "')";
                stmt.addBatch(sql);
              }
            }
            tkCount = 1;
          }
          prevTk = curTk;
        }
      }
    }
    // insert the last one
    if (tkCount >= sortMin) {
      inserts = (tkCount / packMax) + (tkCount % packMax > 0 ? 1 : 0);
      for (int j = 0; j < inserts; j++) {
        sql = "INSERT INTO  " + DmOptions.TMP_DB +"."
            +MainForm.options.toString(DmOptions.DB_PREFIX) + "_tklabels VALUES ('" + prevTk + "')";
        stmt.addBatch(sql);
      }
    }

    m.updateProgress(0);
    in.close();
    m.appendOutput("\nDB is being updating");
    m.IndeterminateProgress(true);
    m.setProgressText("");
    stmt.executeBatch();
    m.IndeterminateProgress(false);
    m.updateProgress(100);
    in.close();

  }
}
