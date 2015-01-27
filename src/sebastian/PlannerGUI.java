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
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

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
	private JButton closestSeeds = null;
	private JButton randomSeeds = null;
	private JButton solveLP = null;
	private JButton solveSA = null;
	private JButton solveGA = null;
	private JList<String> bodies = null;
	private JList<String> treatments = null;
	private JPanel panel = null;
	private SimpleDB db = null;
	private Seed[] seeds = null;
	
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
		panel = new JPanel ();
		loadBody = new JButton ("Load Body");
		measureBody = new JButton ("Measure Body");
		measureAll = new JButton ("Measure All");
		closestSeeds = new JButton ("Closest Seeds");
		randomSeeds = new JButton ("Random Seeds");
		solveLP = new JButton ("Linear Programming");
		solveSA = new JButton ("Simulated Annealing");
		solveGA = new JButton ("Generic Algorithm");
		bodies = new JList<String> ();
		treatments = new JList<String> ();
		
		bodies.addMouseListener(this);
		bodies.setToolTipText("Bodies");
		treatments.addMouseListener(this);
		treatments.setToolTipText("Treatments");
		
		updateBodies ();
		updateTreatments ();
		
		loadBody.addActionListener(this);
		loadBody.setActionCommand("loadBody");
		measureBody.addActionListener(this);
		measureBody.setActionCommand("measureBody");
		measureAll.addActionListener(this);
		measureAll.setActionCommand("measureAll");
		closestSeeds.addActionListener(this);
		closestSeeds.setActionCommand("closestSeeds");
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
		tmp2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.add (tmp2, BorderLayout.EAST);
		
		tmp3.add (closestSeeds);
		tmp3.add (randomSeeds);
		tmp3.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panel.add (tmp3, BorderLayout.SOUTH);
		
		
		tmp4.add (new JScrollPane (treatments));
		tmp4.add (new JScrollPane (bodies));
		tmp4.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));		
		panel.add (tmp4, BorderLayout.WEST);
	}
	
	public PlannerGUI () {
		super ("Treatment Planning");
		db = new SimpleDB ();
		Runtime.getRuntime().addShutdownHook(new Thread () {
			public void run () {
				db.close();
			}
		});
		init ();
		build ();		
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
			System.out.println ("Bodie loaded: " + file);
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
			solver.solveLP();
			db.updateTreatment (entry.getName(), solver.seeds);
			JOptionPane.showMessageDialog(this, "Done!");
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
	    solver.solveSA();
	    db.updateTreatment (entry.getName(), solver.seeds);
	    JOptionPane.showMessageDialog(this, "Done!");
	}

	private void solveGA () {
		BodyEntry entry = null;
		Voxel[][][] body = null;
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		entry = db.getBodyByName(bodies.getSelectedValue());
		if (seeds == null) {
			getRandomSeeds ();
		}
		
		Solver solver = new Solver (entry.getBodyArray(), seeds, entry.getDimensions());		
	    solver.solveGeneticAlg();
	    db.updateTreatment (entry.getName(), solver.seeds);
	    JOptionPane.showMessageDialog(this, "Done!");
	}
	
	private void getClosestSeeds () {
		TreatmentEntry entry = null;
		if (bodies.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(this, "No body selected!");
			return;
		}
		entry = db.getTreatmentByName(bodies.getSelectedValue());
		if (entry.getSeeds() == null || entry.getSeeds().length == 0) {
			JOptionPane.showMessageDialog(this, "No seeds stored! Using random seeds");
			getRandomSeeds ();
		}
		else  {
			this.seeds = entry.getSeeds();
			JOptionPane.showMessageDialog(this, "Done!");
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
		case "closestSeeds":
			getClosestSeeds();
			break;
		case "randomSeeds":
			getRandomSeeds ();
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
