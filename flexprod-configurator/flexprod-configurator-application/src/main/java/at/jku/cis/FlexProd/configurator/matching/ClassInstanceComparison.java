package at.jku.cis.FlexProd.configurator.matching;

public class ClassInstanceComparison {

	private String leftClassInstanceId;
	private String rightClassInstanceId;
	private double score;

	public ClassInstanceComparison() {
	}

	public ClassInstanceComparison(String leftClassInstanceId, String rightClassInstanceId, double score) {
		this.leftClassInstanceId = leftClassInstanceId;
		this.rightClassInstanceId = rightClassInstanceId;
		this.score = score;
	}

	public String getRightClassInstanceId() {
		return rightClassInstanceId;
	}

	public void setRightClassInstanceId(String rightClassInstanceId) {
		this.rightClassInstanceId = rightClassInstanceId;
	}

	public String getLeftClassInstanceId() {
		return leftClassInstanceId;
	}

	public void setLeftClassInstanceId(String leftClassInstanceId) {
		this.leftClassInstanceId = leftClassInstanceId;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}
