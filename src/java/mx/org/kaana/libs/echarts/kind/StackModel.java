package mx.org.kaana.libs.echarts.kind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.echarts.bar.Value;
import mx.org.kaana.libs.echarts.stack.Serie;
import mx.org.kaana.libs.echarts.beans.Axis;
import mx.org.kaana.libs.echarts.beans.Colors;
import mx.org.kaana.libs.echarts.beans.Grid;
import mx.org.kaana.libs.echarts.beans.IMarkLine;
import mx.org.kaana.libs.echarts.beans.Legend;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.echarts.beans.ToolTip;
import mx.org.kaana.libs.echarts.beans.Xaxis;
import mx.org.kaana.libs.echarts.beans.Yaxis;
import mx.org.kaana.libs.echarts.enums.EBarOritentation;
import mx.org.kaana.libs.echarts.model.IDataSet;
import mx.org.kaana.libs.echarts.model.SortNames;
import mx.org.kaana.libs.json.Decoder;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/09/2019
 *@time 09:47:13 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class StackModel extends BaseBarModel implements Serializable {

	private static final long serialVersionUID=-2335254501339126952L;

  private List<Serie> series;
  private List<String> sequence;

  public StackModel() {
    this(new Title("KAJOOL", "Subtitulo"), EBarOritentation.VERTICAL);
  }

  public StackModel(Title title) {
    this(title, EBarOritentation.VERTICAL);
  }

  public StackModel(Title title, IDataSet data) {
    this(title, data.getLegend(), new ArrayList(Arrays.asList(Colors.SERIES_COLORS)), new ToolTip(), new Grid(), data.getXaxis(), new Yaxis(), data.getStack(), EBarOritentation.VERTICAL);
  }

  public StackModel(Title title, IDataSet data, List<String> sequence) {
    this(title, data.getLegend(), new ArrayList(Arrays.asList(Colors.SERIES_COLORS)), new ToolTip(), new Grid(), data.getXaxis(), new Yaxis(), data.getStack(), EBarOritentation.VERTICAL, sequence);
  }

  public StackModel(Title title, IDataSet data, List<String> sequence, List<String> colors) {
    this(title, data.getLegend(), colors, new ToolTip(), new Grid(), data.getXaxis(), new Yaxis(), data.getStack(), EBarOritentation.VERTICAL, sequence);
  }

  public StackModel(Title title, EBarOritentation orientation) {
    this(title, new Legend("2019"), new ArrayList(Arrays.asList(Colors.SERIES_COLORS)), new ToolTip(), new Grid(), new Xaxis(), new Yaxis(), new ArrayList(Arrays.asList(new Serie("2019", Colors.toColor()), new Serie("2020", Colors.toColor()))), orientation);
    this.getLegend().add("2020");
  }

  public StackModel(Title title, IDataSet data, EBarOritentation orientation) {
    this(title, data.getLegend(), new ArrayList(Arrays.asList(Colors.SERIES_COLORS)), new ToolTip(), new Grid(), data.getXaxis(), new Yaxis(), data.getStack(), orientation);
  }

  public StackModel(List<String> color, ToolTip tooltip, Axis xAxis, Axis yAxis, List<Serie> series) {
    this(color, tooltip, xAxis, yAxis, series, EBarOritentation.VERTICAL);
  }

  public StackModel(List<String> color, ToolTip tooltip, Axis xAxis, Axis yAxis, List<Serie> series, EBarOritentation orientation) {
    this(null, null, color, tooltip, new Grid(), xAxis, yAxis, series, orientation);
  }

  public StackModel(Title title, Legend legend, List<String> color, ToolTip tooltip, Grid grid, Axis xAxis, Axis yAxis, List<Serie> series, EBarOritentation orientation) {
    this(title, legend, color, tooltip, grid, xAxis, yAxis, series, orientation, xAxis.getData());
  }

  public StackModel(Title title, Legend legend, List<String> color, ToolTip tooltip, Grid grid, Axis xAxis, Axis yAxis, List<Serie> series, EBarOritentation orientation, List<String> sequence) {
    super(title, legend, color, tooltip, grid, xAxis, yAxis, orientation);
    this.series = series;
    this.sequence = sequence;
    this.loadColors();
  }

  public List<Serie> getSeries() {
    return series;
  }

  public void setSeries(List<Serie> series) {
    this.series = series;
  }

  public String toJson() throws Exception {
    //return StringEscapeUtils.unescapeJava(Decoder.json(this, Boolean.TRUE));
    return StringEscapeUtils.unescapeJava(Decoder.json(this));
  }

  public void addLine(IMarkLine line) {
    if (this.series != null && !this.series.isEmpty()) 
      if(this.series.get(0).getMarkLine()!= null && this.series.get(0).getMarkLine().getData()!= null) 
        this.series.get(0).getMarkLine().getData().add(line);      
  }

  @Override
  public String toString() {
    return "StackModel{" + "series=" + series + ", sequence=" + sequence + '}';
  }

  private void loadColors() {
    super.getColor().clear();
    for (Serie item : this.series) {
      if(item!= null) {
        String color = item.getData().get(0).getItemStyle().getColor();
        item.getLabel().getNormal().setFormatter("{a}\\n{c}");
        item.getLabel().getNormal().setPosition("inside");
        super.getColor().add(color);
        if (this.getOrientation().equals(EBarOritentation.HORIZONTAL)) {
          for (String element : this.getyAxis().getData()) {
            if (!item.getData().contains(new Value(element))) {
              item.getData().add(new Value(element, 0D, color));
            } // if
          } // for
        } // if
        else {
          for (String element : this.getxAxis().getData()) {
            if (!item.getData().contains(new Value(element))) {
              item.getData().add(new Value(element, 0D, color));
            } // if
          } // for
        } // else
      } // if
    } // for
    if (this.getOrientation().equals(EBarOritentation.VERTICAL))
      this.getxAxis().setData(SortNames.toSort(this.getxAxis().getData(), this.sequence));
    else
      this.getyAxis().setData(SortNames.toSort(this.getyAxis().getData(), this.sequence));
    this.sort(this.sequence);
    if (this.series != null && !this.series.isEmpty()) {
      try {
        if(this.series.get(this.series.size() - 1)!= null) {
          Serie serie = this.series.get(this.series.size() - 1).clone();
          int count = 0;
          serie.setName("Total");
          serie.setData(new ArrayList<>());
          if(this.series.get(this.series.size() - 1)!= null) {
            for (Value item : this.series.get(this.series.size() - 1).getData()) {
              serie.getData().add(new Value("KEET:" + this.calculate(count), 0.01D));
              count++;
            } // for
          } // if  
          serie.getLabel().getNormal().setPosition(this.getOrientation().equals(EBarOritentation.VERTICAL) ? "top" : "right");
          this.series.add(serie);
        } // if  
      } // try
      catch (Exception e) {
        Error.mensaje(e);
      } // catch
    } // if	
  }

  private Double calculate(int index) {
    Double regresar = 0D;
    for (Serie item : this.series) {
      if(item!= null) {
        regresar += item.getData().get(index).getValue();
      } // if  
    }	// for	
    return regresar;
  }

  public void sort(final List<String> labels) {
    for (Serie item : this.series) {
      if(item!= null) {
        List<Value> values = new ArrayList<>();
        for (String name : labels) {
          int index = item.getData().indexOf(new Value(name));
          if (index >= 0) {
            values.add(item.getData().get(index));
          }
        } // for
        item.getData().clear();
        item.setData(values);
      } // if  
    } // for
  }

  public void sort() {
    this.sort(this.getxAxis().getData());
  }

  public void sort(final String[] names) {
    this.getxAxis().setData(SortNames.toSort(this.getxAxis().getData(), names));
    this.sort();
  }

  public void toCustomFormatLabel(String format) {
    for (Serie item : this.series) {
      if(item!= null) {
        item.getLabel().getNormal().setFormatter(format);
      } // if  
    } // for
  }

  public void toCustomFontSize(Integer size) {
    for (Serie item : this.series) {
      if(item!= null) {
        item.getLabel().getNormal().setFontSize(size);
        if (item.getMarkLine() != null) {
          item.getMarkLine().getLabel().getNormal().setFontSize(size);
        } // for
      } // if
    } // for
  }

  public void toCustomLabel(String color, Integer size) {
    for (Serie item : this.series) {
      if(item!= null) {
        item.getLabel().getNormal().setColor(color);
        item.getLabel().getNormal().setFontSize(size);
        if (item.getMarkLine() != null) {
          item.getMarkLine().getLabel().getNormal().setFontSize(size);
        } // if
      } // if
    } // for
  }

  public void toCustomUniqueColorTopTotal(String color) {
    for (Serie item : this.series) {
      if(item!= null) {
        item.getLabel().getNormal().setColor(color);
      } // if  
    } // for
  }

  private void toCustomColorSerie() {
    int x = 0;
    for (mx.org.kaana.libs.echarts.bar.Serie item : this.series) {
      if(item!= null) {
        for (Value value : item.getData()) {
          if (x < this.getColor().size()) {
            value.getItemStyle().setColor(this.getColor().get(x));
          }
        } // for
      } // if
      x++;
    } // for
  }

  public void toCustomColorSerie(List<String> colors) {
    int x = 0;
    for (String item : colors) {
      if (x < this.getColor().size()) {
        this.getColor().set(x, item);
      }
      x++;
    } // for
    this.toCustomColorSerie();
  }

  public void removeMarks() {
    for (Serie item: this.series) {
      if(item!= null) 
        item.setMarkPoint(null);
    } // for
  }

  public void removeLines() {
    for (Serie item: this.series) {
      if(item!= null) 
        item.setMarkLine(null);
    } // for
  }

  public void remove() {
    for (Serie item: this.series) {
      if(item!= null) {
        item.setMarkLine(null);
        item.setMarkLine(null);
      } // if
    } // for
  }
  
}
