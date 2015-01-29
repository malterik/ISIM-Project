package sebastian;

import ilog.concert.IloException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import laurin.TreatmentAnalyzer;
import sebastian.ScatterDisplay.ChartType;
import utils.Config;
import utils.Coordinate;
import utils.LogTool;
import utils.RandGenerator;
import utils.Seed;
import utils.Voxel;
import erik.BodyAnalyzer;
import erik.Solver;

public class PlannerGUI extends JFrame implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private JButton loadBody = null;
	private JButton measureBody = null;
	private JButton measureAll = null;
	private JButton storedSeeds = null;
	private JButton randomSeeds = null;
	private JButton closestSeeds = null;
	private JButton solveLP = null;
	private JButton solveSA = null;
	private JButton solveGA = null;
	private JButton trainWeights = null;
	private JButton display = null;
	private JComboBox<ScatterDisplay.ChartType> dispType = null;
	private JList<String> bodies = null;
	private JList<String> treatments = null;
	private JPanel panel = null;
	private SimpleDB db = null;
	private Seed[] seeds = null;
	private JTextArea out = null;
	private Voxel[][][] optBody = null;
	private int[] optDims = null;
	
	private void updateBodies () {
		bodies.removeAll();
		if (db.getBodySize() > 0) {
		  bodies.setListData(db.getBodies ());
		}
		else {
			bodies.setListData(new Vector<String> ());
		}
		this.revalidate ();
	}
	
	private void updateTreatments () {
		treatments.removeAll();
		if (db.getTreatmentSize() > 0) {
		  treatments.setListData(db.getTreatments ());
		}
		else {
			treatments.setListData(new Vector<String> ());
		}
		this.revalidate();
	}
	
	private void init () {
		out = new JTextArea ();
		panel = new JPanel ();
		loadBody = new JButton ("Load Body");
		measureBody = new JButton ("Measure Body");
		measureAll = new JButton ("Measure All");
		closestSeeds = new JButton ("Closest Seeds");
		storedSeeds = new JButton ("Stored Seeds");
		randomSeeds = new JButton ("Random Seeds");
		solveLP = new JButton ("Linear Programming");
		solveSA = new JButton ("Simulated Annealing");
		solveGA = new JButton ("Generic Algorithm");
		bodies = new JList<String> ();
		treatments = new JList<String> ();
		trainWeights = new JButton ("Train Weights");
		display = new JButton ("Show Display");
		dispType = new JComboBox<ScatterDisplay.ChartType>(ScatterDisplay.ChartType.values());
				
		bodies.addMouseListener(this);
		bodies.setToolTipText("Bodies");
		treatments.addMouseListener(this);
		treatments.setToolTipText("Treatments");
				
		closestSeeds.addActionListener(this);
		closestSeeds.setActionCommand("closestSeeds");
		trainWeights.addActionListener (this);
		trainWeights.setActionCommand("trainWeights");
		display.addActionListener(this);
		display.setActionCommand("display");
		loadBody.addActionListener(this);
		loadBody.setActionCommand("loadBody");
		measureBody.addActionListener(this);
		measureBody.setActionCommand("measureBody");
		measureAll.addActionListener(this);
		measureAll.setActionCommand("measureAll");
		storedSeeds.addActionListener(this);
		storedSeeds.setActionCommand("storedSeeds");
		randomSeeds.addActionListener(this);
		randomSeeds.setActionCommand("randomSeeds");
		solveLP.addActionListener(this);
		solveLP.setActionCommand("solveLP");
		solveSA.addActionListener(this);
		solveSA.setActionCommand("solveSA");
		solveGA.addActionListener(this);
		solveGA.setActionCommand("solveGA");
	}
	
	private void build () {
		JPanel tmp1 = new JPanel (new FlowLayout (FlowLayout.CENTER, 5, 5));
		JPanel tmp2 = new JPanel ();
		JPanel tmp3 = new JPanel (new FlowLayout (FlowLayout.CENTER, 5, 5));
		JPanel tmp4 = new JPanel (new GridLayout (0, 1, 5, 5));
		
		panel.setLayout(new BorderLayout (5, 5));
				
		tmp1.add(loadBody);
		tmp1.add(measureBody);
		tmp1.add(measureAll);
		tmp1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.add (tmp1, BorderLayout.NORTH);
		
		tmp2.setLayout(new BoxLayout (tmp2, BoxLayout.Y_AXIS));
		tmp2.add(solveLP);
		tmp2.add(solveSA);
		tmp2.add(solveGA);
		tmp2.add(display);
		tmp2.add(dispType);
		tmp2.add(new JPanel ());
		tmp2.add(new JPanel ());
		tmp2.add(new JPanel ());
		tmp2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.add (tmp2, BorderLayout.EAST);
		
		tmp3.add (closestSeeds);
		tmp3.add (storedSeeds);
		tmp3.add (randomSeeds);
		tmp3.add(trainWeights);
		tmp3.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.add (tmp3, BorderLayout.SOUTH);
		
		
		tmp4.add (new JScrollPane (treatments));
		tmp4.add (new JScrollPane (bodies));
		tmp4.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));		
		panel.add (tmp4, BorderLayout.WEST);
		
		panel.add(new JScrollPane (out), BorderLayout.CENTER);
	}
	
	public PlannerGUI () {
		super ("Treatment Planning");
		Runtime.getRuntime().addShutdownHook(new Thread () {
			public void run () {
				db.close();
			}
		});
		init ();
		build ();
		db = new SimpleDB ();
		updateBodies ();
		updateTreatments ();
		this.setContentPane(panel);
		this.setSize (800, 600);
		this.setResizable(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main (String[] args) {
		new PlannerGUI ();
	}

	private void loadBody () {
		JFileChooser jfc = new JFileChooser (new File (System.getProperty("user.dir")));
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogTitle("Choose body/bodies to load");
		jfc.setMultiSelectionEnabled(true);
		jfc.showDialog(this, "Load");
		jfc.setMultiSelectionEnabled(true);
		
		for (File file: jfc.getSelectedFiles()) {
			LogTool.print ("Loading file " + file.getName (), "debug");
			db.loadBody (file);
		}
		updateBodies();
		JOptionPane.showMessageDialog(this, "Done!");
	}
	
	private void measureBody () {
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		
		
		db.classify (bodies.getSelectedValue());
		updateTreatments ();
		JOptionPane.showMessageDialog(this, "Done!");
	}
	
	private void measureAll () {
		db.classifyAll();
		updateTreatments ();
		JOptionPane.showMessageDialog(this, "Done!");
	}
	
	private void solveLP () {
		BodyEntry entry = null;
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		entry = db.getBodyByName(bodies.getSelectedValue());
		if (seeds == null) {
			getRandomSeeds ();
		}
		
		Solver solver = new Solver (entry.getBodyArray(), seeds, entry.getDimensions());
		try {
			long start = System.currentTimeMillis ();
			solver.solveLP();
			long end = System.currentTimeMillis ();
			db.updateTreatment (entry.getName(), Solver.seeds);
			printSeeds (Solver.seeds);
			this.optBody = Solver.body;
		    this.optDims = Solver.dimensions;
			JOptionPane.showMessageDialog(this, "Done! Analyzing treatment");
			analyzeTreatment(start, end, this.optBody, this.optDims, seeds);
		} catch (IloException iloExc) {
			LogTool.print ("Error solving lp: " + iloExc, "error");
		}
		
	}
	
	private void solveSA () {
		BodyEntry entry = null;
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		entry = db.getBodyByName(bodies.getSelectedValue());
		if (seeds == null) {
			getRandomSeeds ();
		}
		
		Solver solver = new Solver (entry.getBodyArray(), seeds, entry.getDimensions());
		long start = System.currentTimeMillis();
	    solver.solveSA();
	    long end = System.currentTimeMillis();
	    db.updateTreatment (entry.getName(), Solver.seeds);
	    this.optBody = Solver.body;
	    this.optDims = Solver.dimensions;
	    printSeeds (Solver.seeds);
	    JOptionPane.showMessageDialog(this, "Done! Analyzing treatment!");
	    analyzeTreatment(start, end, this.optBody, this.optDims, seeds);
	}

	private void solveGA () {
		BodyEntry entry = null;
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		entry = db.getBodyByName(bodies.getSelectedValue());
		if (seeds == null) {
			getRandomSeeds ();
		}
		
		Solver solver = new Solver (entry.getBodyArray(), seeds, entry.getDimensions());
		long start = System.currentTimeMillis();
	    solver.solveGeneticAlg();
	    long end = System.currentTimeMillis();
	    this.optBody = Solver.body;
	    this.optDims = Solver.dimensions;
	    db.updateTreatment (entry.getName(), Solver.seeds);
	    printSeeds (Solver.seeds);
	    JOptionPane.showMessageDialog(this, "Done! Analyzing treatment");
	    analyzeTreatment(start, end, this.optBody, this.optDims, seeds);
	}
	
	private String printSeed (Seed seed) {
		return String.format ("%.2f;%.2f;%.2f", seed.getX (), seed.getY (), seed.getZ ());
	}
	
	private String printSeeds (Seed[] seeds) {
		String str = "[";
		
		if (seeds != null) {
			if (seeds.length > 0) {
				str += printSeed(seeds[0]);
				for (int i = 1; i < seeds.length; i++) {
					str += "|" + printSeed(seeds[i]);
				}
			}
		}
		
		return str + "]";
	}
	
	private void getStoredSeeds () {
		TreatmentEntry entry = null;
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		entry = db.getTreatmentByName(bodies.getSelectedValue());
		if (entry == null || entry.getSeeds() == null || entry.getSeeds().length == 0) {
			JOptionPane.showMessageDialog(this, "No seeds stored! Using random seeds");
			getRandomSeeds ();
		}
		else  {
			this.seeds = entry.getSeeds();
			JOptionPane.showMessageDialog(this, "Done!");
			LogTool.print(printSeeds (this.seeds), "debug");
		}
	}
	
	private void getRandomSeeds () {
		BodyEntry entry = null;
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		entry = db.getBodyByName(bodies.getSelectedValue());
		seeds = new Seed[Config.numberOfSeeds];
		BodyAnalyzer ba = new BodyAnalyzer(entry.getBodyArray(), entry.getDimensions());
		
		int i = 0;
		while (i < Config.numberOfSeeds ) {
			Coordinate co = new Coordinate(
					RandGenerator.randDouble(ba.getxBoundsTumor(1)[0], ba.getxBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getyBoundsTumor(1)[0], ba.getyBoundsTumor(1)[1]),
					RandGenerator.randDouble(ba.getzBoundsTumor(1)[0], ba.getzBoundsTumor(1)[1]));
			
			if(entry.getBodyArray()[(int)co.getX()][(int)co.getY()][(int)co.getZ()].getBodyType() == Config.tumorType) {
				seeds[i] = new Seed(co.getX(), co.getY(), co.getZ(), 0);
				i++;
			} 
			
		}
		JOptionPane.showMessageDialog(this, "Random seeds done!");
		LogTool.print(printSeeds (this.seeds), "debug");
	}
	
	private void display () {
		if (optBody != null && optDims != null) {
			ScatterDisplay disp = new ScatterDisplay ((ScatterDisplay.ChartType) dispType.getSelectedItem());
			disp.fill(optBody, optDims[0], optDims[1], optDims[2]);
			disp.display();
		}
		else {
			if (bodies.isSelectionEmpty()) {
				JOptionPane.showMessageDialog (this, "No optimized data stored");
			}
			else {
				BodyEntry entry = db.getBodyByName(bodies.getSelectedValue());
				if (entry != null) {
					ScatterDisplay disp = new ScatterDisplay (ScatterDisplay.ChartType.BodyType);
					disp.fill(entry.getBodyArray(), entry.getDimensions()[0], entry.getDimensions()[1], entry.getDimensions()[2]);
					disp.display ();
				}
				else {
					JOptionPane.showMessageDialog (this, "No body selected!");
				}
			}
		}
	}
	
	private void analyzeTreatment (long start, long end, Voxel[][][] body, int[] dimensions, Seed[] seeds) {
		TreatmentAnalyzer ta = new TreatmentAnalyzer(body, dimensions, seeds);
		ta.analyzeAll();
		ta.printResults();
		// runtime measurement
		
		Date date = new Date(end - start);
		DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
		String dateFormatted = formatter.format(date);
		System.out.println("Runtime: " + dateFormatted);
	}
	
	private void getClosestSeeds () {
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog (this, "No body selected! Using random seeds");
			getRandomSeeds ();
		}
		else {
			this.seeds = db.compareBody(bodies.getSelectedValue());
			JOptionPane.showMessageDialog(this, "Done");
		}
		
	}
	
	private void trainWeights () {
		JOptionPane.showMessageDialog(this, "Work in progress!");
		
		// Process: Optimize a set of bodies 3 times for a mean value
		
		// use predefined values
		
		// if new seed position creates faster a result, correct weights
		
	}
	
	@Override
	public void actionPerformed(ActionEvent aEvt) {
		switch (aEvt.getActionCommand()) {
		case "loadBody":
			loadBody ();
			break;
		case "measureBody":
			measureBody ();
			break;
		case "measureAll":
			measureAll ();
			break;
		case "solveLP":
			solveLP ();
			break;
		case "solveSA":
			solveSA ();
			break;
		case "solveGA":
			solveGA ();
			break;
		case "storedSeeds":
			getStoredSeeds();
			break;
		case "randomSeeds":
			getRandomSeeds ();
			break;
		case "closestSeeds":
			getClosestSeeds ();
			break;
		case "display":
			display ();
			break;
		case "trainWeights":
			trainWeights ();
			break;
		default:
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseClicked(MouseEvent mEvt) {
		JList<String> list = (JList<String>) mEvt.getSource();
		if (mEvt.getClickCount() == 2) {
			if (list.getToolTipText().equals ("Bodies")) {
				db.deleteBody(bodies.getSelectedValue());
				updateBodies();
			}
			else if (list.getToolTipText().equals("Treatments")) {
				db.deleteTreatment(treatments.getSelectedValue());
				updateTreatments();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
