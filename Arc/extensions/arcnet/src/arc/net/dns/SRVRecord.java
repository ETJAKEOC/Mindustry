package arc.net.dns;

public record SRVRecord(long ttl, int priority, int weight, int port,
                        String target) implements Comparable<SRVRecord> {

	@Override
	public String toString() {
		return "SRVRecord{" +
				"ttl=" + ttl +
				", priority=" + priority +
				", weight=" + weight +
				", port=" + port +
				", target='" + target + '\'' +
				'}';
	}

	@Override
	public int compareTo(SRVRecord o) {
		if (this.priority != o.priority) {
			return Integer.compare(this.priority, o.priority);
		} else {
			return Integer.compare(this.weight, o.weight);
		}
	}
}
