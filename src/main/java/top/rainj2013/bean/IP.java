package top.rainj2013.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("ip")
public class IP {
	@Column
	private Integer id;
	@Name
	private String address;
	@Column
	private Integer port;
	@Column
	private Boolean verify = false;

	public Boolean getVerify() {
		return this.verify;
	}

	public void setVerify(Boolean verify) {
		this.verify = verify;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public IP(String address, Integer port,boolean verify) {
		super();
		this.address = address;
		this.port = port;
	}

	@Override
	public String toString() {
		return "IP [id=" + id + ", address=" + address + ", port=" + port + ", verify=" + verify + "]";
	}

	public IP() {
	}
}
