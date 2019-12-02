package com.osm_query.query;

public class Member {
	private String type;
	private long ref;
	private String role;
	public Member(long ref_) {
		this.ref = ref_;
	}
	public Member() {}
	public long GetRef() {
		return ref;
	}
	public String GetType() {
		return type;
	}
    @Override
    public boolean equals(Object obj) {
    	return (this.ref == ((Member)obj).GetRef());        
    }
}
