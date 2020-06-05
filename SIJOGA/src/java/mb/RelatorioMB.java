/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import beans.Usuario;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import javax.faces.application.FacesMessage;
import net.sf.jasperreports.engine.JasperExportManager;

/**
 *
 * @author rfabini
 */
@Named(value="relatorioMB")
@ViewScoped()
public class RelatorioMB implements Serializable {
    private String tipo = "Processos Abertos";
    private boolean isAberto = true;
    private Date data1;    
    private Date data2;

    
    
    public void changeTipo(){
        if ("Processos Abertos".equals(this.tipo)){
            this.isAberto = true;
        }
        else{
            this.isAberto = false;
        }
    }

    public boolean isIsAberto() {
        return isAberto;
    }

    public void setIsAberto(boolean isAberto) {
        this.isAberto = isAberto;
    }

    public void gerarRelatorio() throws SQLException, ClassNotFoundException{
         // Recebe o usuario logado
        FacesContext context2 = FacesContext.getCurrentInstance();
        Usuario advogado = (Usuario) context2.getApplication().getExpressionFactory()
        .createValueExpression(context2.getELContext(), "#{loginMB.loggedUser}", Usuario.class)
        .getValue(context2.getELContext());   
        
        // Conexão com o banco
        Connection con = null;
        
        // Define o relatório .jasper e seus parametros
        String arquivoJasper = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if ("Processos Abertos".equals(this.tipo)){
            arquivoJasper = "ProcessosAbertos.jasper";
            params.put("id",advogado.getId());
            params.put("data1", this.data1);
            params.put("data2", this.data2);
        }
        else if ("Processos Encerrados".equals(this.tipo)){
            arquivoJasper = "ProcessosEncerrados.jasper";
            params.put("id",advogado.getId());            
        }
        
        
        try{
            
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/SIJOGA",
            "postgres", "senha123");         
                    
            FacesContext context = FacesContext.getCurrentInstance();    
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();    
            String caminhoRelatorio = servletContext.getRealPath(arquivoJasper);    
            System.out.println("caminhoRelatorio :"+caminhoRelatorio);
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();    
            response.setContentType("application/pdf");    
            response.addHeader("Content-disposition", "attachment; filename=\"relatorio.pdf\"");    
            JasperPrint impressao = JasperFillManager.fillReport(caminhoRelatorio, params, con);    
            System.out.println("impressao :"+impressao);
            JasperExportManager.exportReportToPdfStream(impressao, response.getOutputStream());                        

            context.getApplication().getStateManager().saveView(context);    
            context.renderResponse();
            context.responseComplete();                                                            

        }catch(Exception e){
            FacesMessage msg = new FacesMessage(e.getMessage());
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        finally{
            if(con != null)
                con.close();    
        }
    }
    
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getData1() {
        return data1;
    }

    public void setData1(Date data1) {
        this.data1 = data1;
    }

    public Date getData2() {
        return data2;
    }

    public void setData2(Date data2) {
        this.data2 = data2;
    }
    
    
    
}
