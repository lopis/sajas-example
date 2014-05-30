package enterpriseTest.model;

import java.util.Vector;


/*	SINALPHA
 * 		v260309: usa apenas y(alpha) da f�rmula da Rastilav Lapshin
 * 			valores de alpha entre 3/2PI e 5PI/2, incrementos assim�tricos de xPI/12
 * 		v221010: outcomes: Contract.OutcomeType instead of boolean (JU)
 * 		v271010: include mapping between contract outcome and evidence outcome
 */

public class SinAlphaModel {
		
	
	static double GROWING_PACE = 12;
	static double OMEGA = Math.PI/GROWING_PACE;			// fraction that divides the circle into PI/fr portions
	//static double a = 0.1;   	// function zeros
	static double by = 0.5;  	// y saturation
	double alpha;
	
	static boolean UPDATE_LAMBDA_USING_NEGATIVE_OUTCOMES = true;
	
	
	/**
	 * 
	 * @param useContextual
	 */
	public SinAlphaModel () {
		//System.out.println ("Sinalpha model created.");
	} // constructor

	
	/**
	 * 
	 * -----------------------------
	 * 		getTrust
	 * -----------------------------
	 * 
	 * v20042012: different from ECAI'2012 (now, use always all evidence)
	 * 
	 * TODO: use another algorithm
	 * @return
	 */
	public double getTrust (Vector<Contract> setOfEvidence) {
		double lambda = 1.0;			// incremental factor
		double lambdaFactor = 0.0;		// aggregated number of 'negative' outcomes
		
		// TODO: update better than calculate from beginning?
		// TODO: document this value, do whatever necessary
		alpha = 5.22;

		Contract evidence;

		//System.out.println ("Analisando a tw de novo supplier --------");
		
		
		// loop over all pieces of evidence
		for (int i=0; i < setOfEvidence.size (); i++) {
			
			evidence = setOfEvidence.elementAt(i);

			// update lambda
			
			if (UPDATE_LAMBDA_USING_NEGATIVE_OUTCOMES) {
				// get weighted sum of 'negative outcomes
				lambdaFactor = updateLambdaFactor (evidence, lambdaFactor);
				
				// update lambda
				lambda = getLambda (evidence, lambdaFactor);	
			}
			
			else {
				lambda = Contract.lambdaPerOutcomeType[evidence.getResult().ordinal()];
			}
			
			
			
			// update alpha
			if (alpha + lambda*OMEGA <= 3*Math.PI/2)
				alpha = 3*Math.PI/2;
			else if (alpha + lambda*OMEGA >= 5*Math.PI/2)
				alpha = 5*Math.PI/2;
			else alpha = alpha + lambda*OMEGA;
		}
			

		// debugging to sinAlpha log
		// utils.printResults("] - " + utils.round2Decimal(((by * Math.sin (alpha)+0.5)), 2), true, logFile);
		
		// 0.5 is added to get in [0, 1]
		return ((by * Math.sin (alpha)+0.5));
				
	}

	/**
	 * 
	 * ---------------------------------
	 * 		getLambda
	 * ---------------------------------
	 * 
	 * v20042012: different from ECAI'2012 (now, F is different form Fd)
	 * 
	 * @param evidence
	 * @return
	 */
	private double getLambda (Contract evidence, double lambdaFactor) {

		double lambda;
		
		// determine the increment to alpha
		if (evidence.getResult() == Contract.OutcomeType.F) {
			lambda = Contract.lambdaPerOutcomeType[Contract.OutcomeType.F.ordinal()];
		}
		else {
			// get weighted number of 'negative' outcomes
			lambda = Contract.lambdaPerOutcomeType[evidence.getResult().ordinal()] * (Math.exp(lambdaFactor)/100 + 1);			
		}
		
		//System.out.println (lambdaFactor + " --- " + evidence.getResult() + ": lambda: " + Utils.round2Decimal(lambda, 2));
		return lambda;
	}

	/**
	 * -----------------------------------
	 * 		updateLambdaFactor
	 * -----------------------------------
	 * @param lambdaFactor
	 * @return
	 */
	private double updateLambdaFactor (Contract evidence, double lambdaFactor) {
		
		return lambdaFactor + Contract.lambdaFactorPerOutcomeType[evidence.getResult().ordinal()];

	}
	
} // class SinAlphaModel
