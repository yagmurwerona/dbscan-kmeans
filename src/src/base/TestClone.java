package base;

class TestClone implements Cloneable {
	public TestClone() {
	}

	public TestClone clone() throws CloneNotSupportedException {
		return this.clone();
	}
}