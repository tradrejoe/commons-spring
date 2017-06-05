package org.lc.model.fin;

import java.io.Serializable;

public class CorrGraphLink implements Serializable {

	public static final long serialVersionUID = 0L;
	
	String source;
	String target;
	double corr;
	
	public CorrGraphLink() {}
	
	public CorrGraphLink(String source, String target, double corr) {
		this.source = source;
		this.target = target;
		this.corr = corr;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public double getCorr() {
		return corr;
	}
	public void setCorr(double corr) {
		this.corr = corr;
	}
}
