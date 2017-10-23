package clb.core.whtcustom.dto;

public class Button {
	private String name;  
	  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }

	@Override
	public String toString() {
		return "Button [name=" + name + "]";
	}  
    
}
