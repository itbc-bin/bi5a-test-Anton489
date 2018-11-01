import java.awt.*;
import javax.swing.*;
import java.io.BufferedReader;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * JAVA GCcalculator jaar 2 blok 1
 * Dit programma is in staat een FASTA bestand te lezen met daarin een sequentie.
 * De sequentie wordt vervolgens geanalyseerd op GC percentage.
 * @author Anton Ligterink
 * @since  1 November 2018
 * @version 1.0.0
 */

public class GCcalculator extends JFrame {
    private JButton browseButton = new JButton("<html>Browse<br/>file</html>");
    private JButton readButton = new JButton("<html>Read<br/>selected<br/>file</html>");
    private JButton analyseButton = new JButton("<html>Get<br/>GC%</html>");
    private JTextArea fileVeld;
    private JLabel headerVeld;
    //private JTextArea seqVeld;
    private BufferedReader fileReader;
    private StringBuilder builder;
    private JPanel panel;
    private String seq;
    private String header;
    private JTextArea vanWaarde;
    private JTextArea totWaarde;
    private JLabel vanLabel;
    private JLabel totLabel;




    /**
     * Dit is de main methode van de GCcalculator class.
     * Deze methode maakt een nieuw frame aan met de nodige
     * titel en grootte. Daarnaast wordt er een GUI aangemaakt
     * met de createGUI methode.
     */
    public static void main(String[] args) {
        GCcalculator frame = new GCcalculator();
        frame.setSize(600, 400);
        frame.setTitle("GCcalculator");
        frame.createGUI();
        frame.setVisible(true);
    }

    /**
     * Dit is de createGUI methode van de class GCcalculator.
     * Het graphical user interface wordt aangemaakt en
     * de nodige attributen worden meegegeven:
     * 2 JTextArea's, 3 JButtons en een JPanel.
     */
    private void createGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        window.setLayout(new FlowLayout());
        window.setBackground(new Color(180, 174, 24));
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(550, 80));
        fileVeld = new JTextArea();
        //headerVeld = new JLabel();
        vanWaarde = new JTextArea("");
        totWaarde = new JTextArea("");
        vanLabel = new JLabel("Van: ");
        totLabel = new JLabel("Tot: ");
        JLabel accessiecode = new JLabel("Accessiecode en naam: ");



        fileVeld.setColumns(33);
        fileVeld.setRows(3);
        vanWaarde.setColumns(5);
        vanWaarde.setRows(1);
        totWaarde.setColumns(5);
        totWaarde.setRows(1);

        window.add(browseButton);
        window.add(fileVeld);
        window.add(readButton);
        window.add(vanLabel);
        window.add(vanWaarde);
        window.add(totLabel);
        window.add(totWaarde);
        window.add(analyseButton);
        window.add(panel);
        window.add(accessiecode);
    }


    /**
     * Dit is de constructor van de class GCcalculator.
     * De ActionListener kijkt of een van de drie knoppen word ingedrukt.
     * browseButton: opent een venster waarin een bestand kan worden uitgezocht.
     * readButton: leest het bestand gevonden in het fileVeld in, de output wordt opgeslagen in de seq variable(string).
     * analyseButton: analyseert de sequentie door: 1) te checken of die volledig uit DNA bestaat. 2) Te kijken welk deel
     * er gebruikt moet worden en 3) visualiseert het GC percentage in het JPanel.
     *
     * @throws FileNotFoundException Gebruikt wanneer de file in het fileVeld niet gevonden kan worden.
     */
    private GCcalculator() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                int choice = chooser.showOpenDialog(null);
                if (choice != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                try {
                    if (!chooser.getSelectedFile().getName().endsWith(".fasta")) {
                        throw new Error_exceptie("errorerror");
                    }
                } catch (Error_exceptie e) {
                }
                fileVeld.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    fileReader = new BufferedReader(new FileReader(fileVeld.getText()));
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
                try {

                    builder = new StringBuilder();
                    String line;
                    seq = "";
                    while ((line = fileReader.readLine()) != null) {
                        if (line.startsWith(">")) {
                            header = "";
                            header += line;

                        }
                        else if (!line.startsWith(">")&&(line != null)) {
                            //builder.append(line);
                            seq += line;
                        }
                    }

                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("er is iets mis met de inhoud van het bestand");
                }
                finally {
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        analyseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String dna = seq.replaceAll("\\s", "").toUpperCase();
                checkQuality(dna);
                //ArrayList headerLijst = header.split(" ");
                System.out.println(header);
                String korterDNA = getDnaPart(dna);
                double gc_perc = getGC(korterDNA);
                visualizeGC(gc_perc);


            }
        });
    }

    /**
     * checkQuality is een method van de GCcalculator class.
     * De methode checked of de sequentie wel volledig uit DNA bestaat en geeft anders een exceptie.
     * @param seq De sequentie die gecontroleerd word
     * @exception Geen_DNA Opgebracht als de sequentie niet uit DNA bestaat, er verschijnt een pop-up venster.
     */
    private void checkQuality(String seq) {
        try {
            for (int x = 0; x < seq.length(); x++) {
                if (!Character.toString(seq.charAt(x)).toUpperCase().matches("[ATGC]+")) {
                    System.out.println(Character.toString(seq.charAt(x)).toUpperCase());
                    throw new Geen_DNA("Input is not DNA only");
                }
            }
        } catch (Geen_DNA e) {
        }
    }

    /**
     * Verkrijgt het GC percentage van de ingebrachte sequentie.
     * @param dna De DNA sequentie waar het GC percentage van verkregen word.
     * @return Returned de double genaamd gc_perc.
     */
    private double getGC(String dna) {
        double gc = 0;
        double at = 0;
        for (int x = 0; x < dna.length(); x++) {

            if (Character.toString(dna.charAt(x)).matches("[GC]")) {
                gc += 1;
            }
            else if (Character.toString(dna.charAt(x)).matches("[AT]")) {
                at += 1;
            }
        }
        double gc_perc = gc/(at+gc)*100;
        return gc_perc;
    }

    /**
     * Gebruikt de DNA sequentie en de van en tot waarden om het stuk DNA te bepalen dat geanalyseerd
     * moet worden en returned deze.
     * @param dna De DNA sequentie
     * @return
     */
    private String getDnaPart(String dna) {
        int van = 0;
        int tot = dna.length();
        String korterDNA = "";

        if (!vanWaarde.getText().equals("")) {
            van = Integer.parseInt(vanWaarde.getText());
        }
        if (!totWaarde.getText().equals("")) {
            tot = Integer.parseInt(totWaarde.getText());
        }
        try {
            for (int x = van; x < tot; x++) {
                korterDNA += Character.toString(dna.charAt(x));
            }
        } catch (StringIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Wrong parameters");
        }
        if (tot<van) {
            JOptionPane.showMessageDialog(null, "Wrong parameters");
        }

        return korterDNA;

    }

    /**
     * Visualiseert de GC percentage balk, met behulp van de double gcPerc.
     * @param gcPerc Het GC percentage, is een double.
     */
    private void visualizeGC(double gcPerc) {
        Graphics paper = panel.getGraphics();
        paper.setColor(Color.white);
        paper.fillRect(-500,-500, 2000, 2000);
        paper.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        paper.setColor(Color.black);
        paper.drawString("0%", 10, 20);
        paper.drawString("100%", 510, 20);
        paper.drawString(Double.toString(gcPerc) , 260, 15);

        paper.setColor(Color.BLUE);
        double gcDouble = gcPerc/100*530;
        int gcInt = (int) gcDouble;
        paper.fillRect(10,30,gcInt+10,60);
    }
}



/**
 *Custom exceptie voor wanneer de sequentie niet uit DNA bestaat.
 */
class Geen_DNA extends Exception {
    protected Geen_DNA(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
/**
 *Custom exceptie voor wanneer er een error ontstaat.
 */
class Error_exceptie extends Exception {
    protected Error_exceptie(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}