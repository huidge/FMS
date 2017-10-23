package clb.core.system.dto;


import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SysUserCustomFuncs  {

    public List<SysUserCustomFunction> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<SysUserCustomFunction> functionList) {
        this.functionList = functionList;
    }

    private List<SysUserCustomFunction> functionList;

    public ClbUser getClbUser() {
        return clbUser;
    }

    public void setClbUser(ClbUser clbUser) {
        this.clbUser = clbUser;
    }

    private ClbUser clbUser;

}
