package clb.core.order.dto;

import java.util.List;


public class MenuAuth{

	/**
	 * 唯一序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 菜单id
	 */
	private Long id;

	/**
	 * 父id
	 */
	private Long parentId;
	/**
	 * 菜单名称
	 */
	private String meaning;
	
	/**
	 * 父菜单名称
	 */
	private String parentMeaning;
	
	/**
	 * 查询状态
	 */
	private String queryFlag;
	
	/**
	 * 更新状态
	 */
	private String updateFlag;
	/**
	 * 子菜单
	 */
	private List<MenuAuth> children;
	
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	public String getParentMeaning() {
		return parentMeaning;
	}
	public void setParentMeaning(String parentMeaning) {
		this.parentMeaning = parentMeaning;
	}
	
	public List<MenuAuth> getChildren() {
		return children;
	}
	public void setChildren(List<MenuAuth> children) {
		this.children = children;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getQueryFlag() {
		return queryFlag;
	}
	public void setQueryFlag(String queryFlag) {
		this.queryFlag = queryFlag;
	}
	public String getUpdateFlag() {
		return updateFlag;
	}
	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}
