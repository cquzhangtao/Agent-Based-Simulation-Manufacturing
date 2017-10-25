package manu.utli;

public class KeyValue<K, V extends Comparable<V> > implements Comparable<KeyValue<K,V>> {
	K key;
	V value;
	
	public KeyValue(K key,V value){
		this.key =key;
		this.value=value;
	}
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int compareTo(KeyValue<K,V> o) {
		// TODO Auto-generated method stub
		return value.compareTo( o.getValue());
	}

}
