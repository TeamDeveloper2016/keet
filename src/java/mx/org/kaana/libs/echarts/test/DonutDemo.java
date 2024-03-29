package mx.org.kaana.libs.echarts.test;

import java.util.Collections;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.echarts.kind.DonutModel;
import mx.org.kaana.libs.echarts.kind.PieModel;
import mx.org.kaana.libs.echarts.model.Datas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/09/2019
 *@time 10:01:14 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class DonutDemo {

	  private static final Log LOG=LogFactory.getLog(DonutDemo.class);
		
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
      PieModel model= new DonutModel("serie", "55%", "40%", new Title("KAJOOL", null));
			((DonutModel)model).toCustomDisplay("1,234", "56.12 %", "1,234");
//			((DonutModel)model).toCustomDonut("1,234");
//			model.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'percent');}");
			LOG.info(model.toJson());
			
			Datas datas  = new Datas("ventas", DaoFactory.getInstance().toEntitySet("VistaEchartsDemostracionDto", "simple", Collections.EMPTY_MAP));
  		PieModel data= new DonutModel("ventas", "55%", "40%", datas);
			LOG.info(data.toJson());
    }

}
