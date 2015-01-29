package laurin;

import ilog.concert.IloException;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import laurin.CplexSolver.BoundType;

import erik.Solver;

import sebastian.BodyEntry;
import utils.Config;
import utils.Seed;
import utils.Voxel;


public class LPStepwiseGUI extends JFrame implements ActionListener{

	public static enum DoseGoalType {GOAL_DOSE, MIN_DOSE, MAX_DOSE, MANUALLY};
	public static enum BodyType {NORMAL, SPINE, LIVER, PANCREAS, TUMOR};
	public static enum ActionType {OPTIMIZE};
	
	private CplexSolver cplexSolverSampled;
	private Voxel[][][] body;
	private int[] dimensions;
	
	private static final long serialVersionUID = 1L;
	private JPanel panel = null;
	private JComboBox<LPStepwiseGUI.DoseGoalType> doseGoalBox;
	private JComboBox<CplexSolver.BoundType> boundBox;
	private JComboBox<CplexSolver.SlackType> slackBox;
	private Checkbox[] bodyTypeBoxes;
	private CheckboxGroup bodyTypeBoxesGroup;
	private JTextField[] lowerBoundsFields;
	private JTextField[] upperBoundsFields;
	private JTextField manualDoseField;

	private JLabel boundBoxText;
	private JLabel slackBoxText;
	private JLabel doseGoalBoxText;
	private JLabel bodyTypeBoxesText;
	private JLabel[] bodyBoundsTexts;
	private JLabel bodyBoundsHeadLine;
	private JLabel lowerBoundHeadLine;
	private JLabel upperBoundHeadLine;
	
	private JButton optimizeButton;
	private JButton showAnalysisButton;

	
	ArrayList<TreatmentAnalyzer> treatmentAnalyzers;

	
    public LPStepwiseGUI(CplexSolver cplexSolverSampled, Voxel[][][] body) throws IloException {
		super ("Treatment Planning");
		this.cplexSolverSampled = cplexSolverSampled;	
		this.body = body;
		this.dimensions = new int[]{body.length, body[0].length, body[0][0].length};
		this.treatmentAnalyzers = new ArrayList<TreatmentAnalyzer>();
		cplexSolverSampled.initialSolution();
		Seed[] initalSeeds = cplexSolverSampled.getCurrentSeeds();
		treatmentAnalyzers.add(new TreatmentAnalyzer(body, dimensions, initalSeeds, "Initial"));
		TreatmentAnalyzer.printTreatmentComparison(treatmentAnalyzers, true);
		
		Runtime.getRuntime().addShutdownHook(new Thread () {
			public void run () {
			}
		});
		init();
		build();
		this.setContentPane(panel);
		this.setSize (500, 400);
		this.setResizable(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
    }
    
	private void init () throws IloException {
		panel = new JPanel ();
		
		// selection boxes
		boundBox = new JComboBox<CplexSolver.BoundType>(CplexSolver.BoundType.values());
		slackBox = new JComboBox<CplexSolver.SlackType>(CplexSolver.SlackType.values());
		doseGoalBox = new JComboBox<LPStepwiseGUI.DoseGoalType>(LPStepwiseGUI.DoseGoalType.values());
		bodyTypeBoxes = new Checkbox[Config.tumorType];
		bodyTypeBoxesGroup = new CheckboxGroup();
		for (int i = 0; i < Config.tumorType; i++)
			bodyTypeBoxes[i] = new Checkbox(Config.bodyTypeDescriptions[i], bodyTypeBoxesGroup, false);
		
		// texts
		boundBoxText = new JLabel("Bound type: ");
		slackBoxText = new JLabel("Slack type: ");
		doseGoalBoxText = new JLabel("Dose goal: ");
		bodyTypeBoxesText = new JLabel("Body types: ");
		
		manualDoseField = new JTextField();
		manualDoseField.setEnabled(false);
		lowerBoundsFields = new JTextField[Config.tumorType];
		upperBoundsFields = new JTextField[Config.tumorType];
		bodyBoundsTexts = new JLabel[Config.tumorType];
		for (int i = 0; i < Config.tumorType; i++)
		{
			final JTextField textField;
			lowerBoundsFields[i] = new JTextField();
			upperBoundsFields[i] = new JTextField();
			bodyBoundsTexts[i] = new JLabel(Config.bodyTypeDescriptions[i]);
		}
		
		updateBoundFields(cplexSolverSampled.getLowerBounds(), cplexSolverSampled.getUpperBounds());
		bodyBoundsHeadLine = new JLabel("Body type");
		lowerBoundHeadLine = new JLabel("Lower Bound");
		upperBoundHeadLine = new JLabel("Upper Bound");
		
		// buttons
		optimizeButton = new JButton("Optimize");
		showAnalysisButton = new JButton("Show Analysis");
		
		doseGoalBox.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){checkDoseGoalField();}});
		
		optimizeButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){try {
			optimize();
		} catch (IloException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}}});
		
		showAnalysisButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){showAnalysis();}});
	}
	
	private void build () {
		JPanel tmp;
		JPanel tmp1 = new JPanel ();
		tmp1.setLayout(new BoxLayout (tmp1, BoxLayout.Y_AXIS));
		
		// add bound box
		tmp = new JPanel();
		tmp.setLayout(new GridLayout(0,3));
		tmp.add(boundBoxText);
		tmp.add(boundBox);
		tmp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		tmp1.add(tmp);
		
		// add slack box
		tmp = new JPanel();
		tmp.setLayout(new GridLayout(0,3));
		tmp.add(slackBoxText);
		tmp.add(slackBox);
		tmp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		tmp1.add(tmp);
		
		// add dose goal box
		tmp = new JPanel();
		tmp.setLayout(new GridLayout(0,3));
		tmp.add(doseGoalBoxText);
		tmp.add(doseGoalBox);
		tmp.add(manualDoseField);
		tmp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		tmp1.add(tmp);
		
		// add body type box
		tmp = new JPanel (new FlowLayout (FlowLayout.CENTER, 6, 1));
		tmp.add(bodyTypeBoxesText);
		for (int i = 0; i < Config.tumorType; i++)
			tmp.add(bodyTypeBoxes[i]);
		tmp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		tmp1.add(tmp);

		// add optimize
		tmp = new JPanel (new FlowLayout (FlowLayout.CENTER, 6, 1));
		tmp.add(optimizeButton);
		tmp.add(showAnalysisButton);
		tmp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		tmp1.add(tmp);
		
		tmp = new JPanel();
		tmp.setLayout(new GridLayout(0,3));

		//GridLayout experimentLayout = new GridLayout(0,2);
		tmp.add(bodyBoundsHeadLine);
		tmp.add(lowerBoundHeadLine);
		tmp.add(upperBoundHeadLine);
		
		for (int i = 0; i < Config.tumorType; i++)
		{
			tmp.add(bodyBoundsTexts[i]);
			tmp.add(lowerBoundsFields[i]);
			tmp.add(upperBoundsFields[i]);
		}
		
		tmp1.add(tmp);
		
		panel.setLayout(new BorderLayout (0, 5));
		panel.add (tmp1, BorderLayout.NORTH);
	}
	
	/*public void actionPerformed(ActionEvent aEvt) {
		switch (aEvt.getActionCommand()) {
		case "optimize":
			optimize();
			break;
		}
	}*/
	
	private double getConfigDose(int bodyType, LPStepwiseGUI.DoseGoalType doseGoalType)
	{
		double dose = 0.0;
		
		switch(bodyType)
		{
			case Config.normalType: {
				switch (doseGoalType)
				{
					case GOAL_DOSE:
						dose = Config.normalGoalDose;
						break;
					case MAX_DOSE:
						dose = Config.normalMaxDose;
						break;
					case MIN_DOSE:
						dose = Config.normalMinDose;
						break;
				}
				break;
			}
			case Config.spineType: {
				switch (doseGoalType)
				{
					case GOAL_DOSE:
						dose = Config.spineGoalDose;
						break;
					case MAX_DOSE:
						dose = Config.spineMaxDose;
						break;
					case MIN_DOSE:
						dose = Config.spineMinDose;
						break;
				}
				break;
			}
			case Config.liverType: {
				switch (doseGoalType)
				{
					case GOAL_DOSE:
						dose = Config.liverGoalDose;
						break;
					case MAX_DOSE:
						dose = Config.liverMaxDose;
						break;
					case MIN_DOSE:
						dose = Config.liverMinDose;
						break;
				}
				break;
			}
			case Config.pancreasType: {
				switch (doseGoalType)
				{
					case GOAL_DOSE:
						dose = Config.pancreasGoalDose;
						break;
					case MAX_DOSE:
						dose = Config.pancreasMaxDose;
						break;
					case MIN_DOSE:
						dose = Config.pancreasMinDose;
						break;
				}
				break;
			}
			case Config.tumorType: {
				switch (doseGoalType)
				{
					case GOAL_DOSE:
						dose = Config.tumorGoalDose;
						break;
					case MAX_DOSE:
						dose = Config.tumorMaxDose;
						break;
					case MIN_DOSE:
						dose = Config.tumorMinDose;
						break;
				}
				break;
			}
		}
		return dose;
	}
	
	private void checkDoseGoalField()
	{
		if (((LPStepwiseGUI.DoseGoalType) doseGoalBox.getSelectedItem()) ==  LPStepwiseGUI.DoseGoalType.MANUALLY)
			manualDoseField.setEnabled(true);
		else
			manualDoseField.setEnabled(false);
	}
	
	private void showAnalysis()
	{
		TreatmentAnalyzer ta = new TreatmentAnalyzer(body, dimensions, cplexSolverSampled.getCurrentSeeds(), "Step " + (treatmentAnalyzers.size()+1));
		treatmentAnalyzers.add(ta);
		TreatmentAnalyzer.printTreatmentComparison(treatmentAnalyzers, true);
	}
	
	private void optimize() throws IloException {
		int bodyType = 0;
		double dose = 0.0;
		
		
		Checkbox selectedCheckBox = bodyTypeBoxesGroup.getSelectedCheckbox();
		for (int i = 0; i < Config.tumorType; i++)
		{
			if (selectedCheckBox == bodyTypeBoxes[i])
				bodyType = i;
		}
		
		LPStepwiseGUI.DoseGoalType doseGoalType = (LPStepwiseGUI.DoseGoalType) doseGoalBox.getSelectedItem();

		
		if (doseGoalType == LPStepwiseGUI.DoseGoalType.MANUALLY)
		{
			dose = Double.parseDouble(manualDoseField.getText());
		}
		else
		{
			dose = getConfigDose(bodyType+1, doseGoalType);
		}
		
		for (int i = 0; i < Config.tumorType; i++)
		{
			cplexSolverSampled.setNewBounds(i+1, CplexSolver.BoundType.LOWER_BOUND, Double.parseDouble(lowerBoundsFields[i].getText()));
			cplexSolverSampled.setNewBounds(i+1, CplexSolver.BoundType.UPPER_BOUND, Double.parseDouble(upperBoundsFields[i].getText()));
		}
		
		cplexSolverSampled.doOptimization(bodyType+1, CplexSolver.GoalType.MINIMIZE, (CplexSolver.SlackType) slackBox.getSelectedItem(), (CplexSolver.BoundType) boundBox.getSelectedItem(), dose);
		cplexSolverSampled.printBounds();
		updateBoundFields(cplexSolverSampled.getLowerBounds(), cplexSolverSampled.getUpperBounds());
	}
	
	private void updateBoundFields(double[] lowerBounds, double[] upperBounds)
	{
		for (int i = 0; i < Config.tumorType; i++)
		{
			lowerBoundsFields[i].setText(Double.toString(lowerBounds[i]));
			upperBoundsFields[i].setText(Double.toString(upperBounds[i]));
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}};