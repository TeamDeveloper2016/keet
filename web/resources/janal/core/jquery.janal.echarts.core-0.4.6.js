/*
 *company KAANA
 *project KAJOOL (Control system polls)
 *date 05/11/2019
 *time 11:15:55 PM
 *author Team Developer 2016 <team.developer@kaana.org.mx>
 */
(function (window) {
  var jsEcharts;

  Janal.Control.Echarts = {
    group: 'Kajool',
    names: {}
  };

  Janal.Control.Echarts.Core = Class.extend({
    RESERVED_ID: 'items',
    RESERVED_NAMES: 'json',
    RESERVED_TITLE: 'title',
    RESERVED_SUBTITLE: 'subTitle',
    RESERVED_OPERATION: 'operation',
    RESERVED_AVERAGE: 'average',
    RESERVED_HEADER: 'keet-item-title',
    RESERVED_SUB_HEADER: 'keet-item-subTitle',
    RESERVED_GROUP: 'group',
    RESERVED_KEY: 'KEET',
    RESERVED_SYMBOL: 'keet-item-symbol',
    RESERVED_CAROUSEL: 'keet-item-carousel',
    RESERVED_HIDE: 'keet-item-hide',
    LABEL_TOKEN: 'KEET:',
    nacional: '#iconoNacional',
    georreferencia: '#iconoInformacion',
    charts: {},
    backup: {},
    history: {},
    selected: {
      group: ''
    },
    carousel: {
      index: 0,
      top: 0,
      items: []
    },
    init: function (names, group) { // Constructor			
      $echarts = this;
      this.charts = names;
      if (typeof (group) === 'undefined')
        this.selected.group = this.RESERVED_KEY;
      Object.assign(this.backup, names);
    }, // init
    start: function () {
      var items = $('.' + this.RESERVED_CAROUSEL);
      if (items.length > 0) {
        $.each(items, function () {
          $(this).addClass($echarts.RESERVED_HIDE);
          $echarts.carousel.items.push($(this).attr('id'));
        });
        this.carousel.top = items.length - 1;
        this.show(this.carousel.items[0]);
      } // if	
    },
    display: function () {
      if ($("#index").length > 0)
        if (this.carousel.items.length > 0)
          //$("#index").html('gráfica: ['+ this.carousel.items[this.carousel.index]+ '] '+ this.carousel.index+ ' de '+ this.carousel.top);
          $("#index").html('Gr\u00E1fica: ' + (this.carousel.index + 1) + ' de ' + (this.carousel.top + 1));
        else
          $("#index").html((this.carousel.index + 1) + ' de ' + (this.carousel.top + 1));
    },
    show: function (id) {
      var items = $('[id="' + id + '"]');
      if (items.length > 0)
        $.each(items, function () {
          $(this).removeClass($echarts.RESERVED_HIDE);
        });
      this.display();
    },
    hide: function (id) {
      var items = $('[id="' + id + '"]');
      if (items.length > 0)
        $.each(items, function () {
          $(this).addClass($echarts.RESERVED_HIDE);
        });
    },
    inc: function (group) {
      var ok = true;
      if (typeof (group) === 'undefined')
        group = this.selected.group;
      this.hide(this.carousel.items[this.carousel.index]);
      if (this.carousel.index < this.carousel.top)
        this.carousel.index++;
      else
        this.carousel.index = 0;
      this.show(this.carousel.items[this.carousel.index]);
      this.display();
      if (this.paint(this.carousel.items[this.carousel.index], group, true))
        ok = false;
      else
        this.single(this.carousel.items[this.carousel.index], group);
      return ok;
    },
    dec: function (group) {
      var ok = true;
      if (typeof (group) === 'undefined')
        group = this.selected.group;
      this.hide(this.carousel.items[this.carousel.index]);
      if (this.carousel.index > 0)
        this.carousel.index--;
      else
        this.carousel.index = this.carousel.top;
      this.show(this.carousel.items[this.carousel.index]);
      this.display();
      if (this.paint(this.carousel.items[this.carousel.index], group, true))
        ok = false;
      else
        this.single(this.carousel.items[this.carousel.index], group);
      return ok;
    },
    begin: function (paint) {
      if (this.carousel.items.length > 0 && this.carousel.index < this.carousel.items.length)
        this.hide(this.carousel.items[this.carousel.index]);
      this.carousel.index = 0;
      if (this.carousel.items.length > 0)
        this.show(this.carousel.items[this.carousel.index]);
      this.display();
      if (typeof (paint) !== 'undefined' && paint)
        this.paint(this.carousel.items[this.carousel.index], this.selected.group, true);
    },
    single: function (id, group) {
      if (typeof refreshEChartSingle !== "undefined") {
        janal.bloquear();
        refreshEChartSingle(id, group);
      } // if	
    },
    load: function (names) {
      if (typeof (names) !== 'undefined') {
        this.charts = names;
        Object.assign(this.backup, names);
      } // if	
      $.each(this.charts, function (id, value) {
        if (id === $echarts.RESERVED_ID)
          $echarts.search(value);
        else
          $echarts.create(id, value);
        $echarts.reserved(id, value);
      });
    },
    frames: function () {
      if (this.charts.hasOwnProperty($echarts.RESERVED_ID))
        this.search(this.charts[$echarts.RESERVED_ID]);
    },
    search: function (value) {
      if (value.hasOwnProperty(this.RESERVED_NAMES))
        $.each(value[this.RESERVED_NAMES], function (id, value) {
          var items = $('[id="' + id + '"]');
          if (items.length > 0)
            $.each(items, function () {
              $(this).html(value + ($(this).hasClass($echarts.RESERVED_SYMBOL) ? ' %' : ''));
            });
        });
    },
    create: function (id, value) {
      if ($('#' + id).length > 0 && value.hasOwnProperty(this.RESERVED_NAMES)) {
        window[id] = echarts.init(document.getElementById(id), {renderer: 'svg', width: 'auto', height: 'auto'});
        window[id].showLoading({text: 'Se est\u00E1 construyendo la gr\u00E1fica ... por favor espere !'});
        window[id].setOption(value[this.RESERVED_NAMES], true);
        window[id].on('click', 'series', function (params) {
          params.chart = id;
          $echarts.send(params);
        });        
        window[id].on('legendselectchanged', function (params) {
          params.chart = id;          
          if ((value.hasOwnProperty($echarts.RESERVED_OPERATION) && value[$echarts.RESERVED_OPERATION] === $echarts.RESERVED_AVERAGE)) {
             params.operation = $echarts.RESERVED_AVERAGE; 
          }
          $echarts.sendLegend(params);
        });
        window[id].hideLoading();
        this.title(id, value);
        this.subTitle(id, value);
      } // id
      else
        console.info('El marco [' + id + '] de la grafica no existe !');
    },
    reserved: function (id, value) {
      // si se definio un agrupador se mete al historial para ya no ir la backend
      if (!value.hasOwnProperty(this.RESERVED_GROUP))
        value[this.RESERVED_GROUP] = this.RESERVED_KEY;
      if (!this.history[value[this.RESERVED_GROUP]])
        this.history[value[this.RESERVED_GROUP]] = {};
      this.history[value[this.RESERVED_GROUP]][id] = value;
    },
    send: function (params) {
      var json = {
        chart: params.chart,
        color: params.color,
        name: params.name,
        value: params.value,
        percent: params.percent,
        dataIndex: params.dataIndex,
        dataType: params.dataType,
        seriesId: params.seriesId,
        seriesIndex: params.seriesIndex,
        seriesName: params.seriesName,
        seriesType: params.seriesType,
        event: params.type
      };
      if (typeof refreshEChartFrame !== "undefined")
        refreshEChartFrame(JSON.stringify(json));
      else
        console.info('No existe la funcion de javascript llamada [refreshEChartFrame]');
    },
    sendLegend: function (params) {
      var selected = new Array();
      var x = null;
      for (x in params.selected) {
        selected.push({"name": x, "display": params.selected[x]});
      } // for
      var json = {
        chart: params.chart,
        selected: selected,
        type: params.type
      };
      $echarts.fix(params);
      if (typeof refreshEChartLegend !== "undefined")
        refreshEChartLegend(JSON.stringify(json));
      else
        console.info('No existe la funcion de javascript llamada [refreshEChartLegend]');
    },
    calculate: function (serie) {
      var dataTemporal = null;
      var totalAcumulate = new Array(serie[0].data.length);
      for (var i = 0; i <= totalAcumulate.length - 1; i++) {
        totalAcumulate[i] = 0.0;
      }// for   
      for (var i = 0; i < serie.length - 1; i++) {
        dataTemporal = serie[i].data;
        for (var j = 0; j <= dataTemporal.length - 1; j++) {
          if ((janal.isInteger(dataTemporal[j]).value) || janal.isDouble(dataTemporal[j].value)) {
            totalAcumulate[j] = totalAcumulate[j] + dataTemporal[j].value;
          } // if   
        }// for        
      } // for
      for (var i = 0; i <= totalAcumulate.length - 1; i++) {
        serie[serie.length - 1].data[i].name = 'KEET:' + totalAcumulate[i];
        serie[serie.length - 1].data[i].value = 0.01;
        if (serie.length === 1) {
          serie[serie.length - 1].data[i].name = 'Total:' + totalAcumulate[i];
          serie[serie.length - 1].data[i].value = 0;
        } // if
      } // for      

    },
     average: function (serie) {
      var dataTemporal = null;
      var totalAcumulate = new Array(serie[0].data.length);
      var contador = 0;
      for (var i = 0; i <= totalAcumulate.length - 1; i++) {
        totalAcumulate[i] = 0.0;
      }// for   
      for (var i=0;i <= totalAcumulate.length - 1;i++) {
        contador=0;
        for (var j = 0; j < serie.length - 1; j++) {
          dataTemporal = serie[j].data;         
          if ((janal.isInteger(dataTemporal[i]).value) || janal.isDouble(dataTemporal[i].value)) {
            totalAcumulate[i]=totalAcumulate[i]+dataTemporal[i].value;
            contador = contador+1;
          } // if
        }// for
        if (contador!==0 && totalAcumulate[i]!==0) {
          totalAcumulate[i]=$echarts.format({value:totalAcumulate[i]/contador,name:'average'},'double-one-decimal');
        } // if  
      }// for      
      for (var i = 0; i <= totalAcumulate.length - 1; i++) {
        serie[serie.length - 1].data[i].name = 'KEET:' + totalAcumulate[i];
        serie[serie.length - 1].data[i].value = 0.01;
        if (serie.length === 1) {
          serie[serie.length - 1].data[i].name = 'Total:' + totalAcumulate[i];
          serie[serie.length - 1].data[i].value = 0;
        } // if
      } // for      
    },   
    deletedSerie: function (series, deleted) {
      var serieClean = new Array();
      var x = null;
      for (x in deleted) {
        delete series[deleted[x]];
      } // for      
      for (x = 0; x <= series.length - 1; x++) {
        if (typeof series[x] !== 'undefined') {
          serieClean.push(series[x]);
        } // if   
      } // for
      return serieClean;
    },
    checkFix: function (serieData) {
      var ok = false;
      for (y = 0; y <= serieData.length - 1; y++) {
        ok = serieData[y].name.toUpperCase() === 'TOTAL';
        if (ok)
          break;
      }
      return ok;
    },
    validate: function (params, serieData) {
      var x = null;
      var ok = false;
      for (x in params.selected) {
        ok = params.selected[x];
        if (ok) {
          serieData[serieData.length - 1].data;
        } // if

      } // for      
    },
    fix: function (params) {
      var chart = window[params.chart];
      var seriesData = chart.getOption().series;
      var serieDeleted = new Array();
      var x = null;
      if ($echarts.checkFix(seriesData)) {
        for (x in params.selected) {
          for (y = 0; y <= seriesData.length - 1; y++) {
            if (x.toUpperCase() === seriesData[y].name.toUpperCase()) {
              if (!params.selected[x]) {
                serieDeleted.push(y);
              } // if
            } // if          
          } // for        
        } // for
        seriesData = $echarts.deletedSerie(seriesData, serieDeleted);
        if (params.hasOwnProperty('operation') && params['operation']==='average') {
          $echarts.average(seriesData);
        } // if
        else {
          $echarts.calculate(seriesData);
        } // if
        chart.setOption({series: seriesData});
      } // if  
    },
    refresh: function (items, look) {
      if (typeof (look) === 'undefined')
        look = true;
      // esto es para actualizar varias graficas donde sus datos cambiaron
      $.each(items, function (id, value) {
        if (id === $echarts.RESERVED_ID)
          $echarts.search(value);
        else
          $echarts.update(id, value, look);
      });
    },
    title: function (id, value) {
      if (typeof (value) !== 'undefined')
        if (value.hasOwnProperty(this.RESERVED_TITLE))
          if ($('#' + id + '-' + this.RESERVED_TITLE).length > 0)
            $('#' + id + '-' + this.RESERVED_TITLE).html(value[this.RESERVED_TITLE]);
          else
          if ($('.' + this.RESERVED_HEADER).length > 0)
            $('.' + this.RESERVED_HEADER).html(value[this.RESERVED_TITLE]);
    },
    subTitle: function (id, value) {
      if (typeof (value) !== 'undefined')
        if (value.hasOwnProperty(this.RESERVED_SUBTITLE))
          if ($('#' + id + '-' + this.RESERVED_SUBTITLE).length > 0)
            $('#' + id + '-' + this.RESERVED_SUBTITLE).html(value[this.RESERVED_SUBTITLE]);
          else
          if ($('.' + this.RESERVED_SUB_HEADER).length > 0)
            $('.' + this.RESERVED_SUB_HEADER).html(value[this.RESERVED_SUBTITLE]);
    },
    update: function (id, value, look) {
      if (typeof (look) === 'undefined')
        look = true;
      // esto es para actualizar una sola grafica porque sus datos cambiaron
      if (window[id]) {
        if (value.hasOwnProperty(this.RESERVED_NAMES)) {
          this.charts[id] = value;
          window[id].clear();
          window[id].setOption(value[this.RESERVED_NAMES]);
          window[id].on('click', 'series', function (params) {
            params.chart= id;
            if (typeof(id)=== 'undefined')
              $echarts.send(params);
          });        
          window[id].on('legendselectchanged', function (params) {
            params.chart= id;          
            if ((value.hasOwnProperty($echarts.RESERVED_OPERATION) && value[$echarts.RESERVED_OPERATION] === $echarts.RESERVED_AVERAGE))
              params.operation = $echarts.RESERVED_AVERAGE; 
            if (typeof(id)=== 'undefined')
              $echarts.sendLegend(params);
          });
          window[id].hideLoading();
          this.title(id, value);
          this.subTitle(id, value);
          if (look)
            this.reserved(id, value);
        } // if	
      } // if	
      else
        console.info('El marco [' + id + '] de la grafica no existe !');
    },
    resize: function (id) {
      if (window[id]) {
        window[id].resize();
      } // if	
      else
        console.info('El marco [' + id + '] de la grafica no existe !');
    },
    label: function (value) {
      //value = this.capital(value);
      return value.length > 12 ? value.replace(/\s/g, '\n') : value;
    },
    upperCase: function (value) {
      value = value.toUpperCase();
      return value.length > 12 ? value.replace(/\s/g, '\n') : value;
    },
    freeLabel: function (value) {
      if (value.length > 75)
        value = value.substring(0, 75) + '...';
      return value.length > 12 ? value.replace(/\s/g, '\n') : value;
    },
    capital: function (text) {
      if (typeof (text) === 'string') {
        text = text.toLowerCase();
        text = text.charAt(0).toUpperCase() + text.slice(1);
        if (text.length > 75)
          text = text.substring(0, 75) + '...';
      } // if
      return text;
    },
    labelMarkLine: function (params) {
      var data = parseFloat(params.data.value);
      var text = '  ' + params.data.name + '\n' + data.toLocaleString('en-US', {style: 'decimal', minimumFractionDigits: 0, maximumFractionDigits: 0});
      return text;
    },
    format: function (params, type, all) {
      // params.seriesName
      // params.namex
      // params.value
      // params.data is Object
      // params.color
      if (typeof (type) === 'undefined')
        type = '';
      if (typeof (all) === 'undefined')
        all = true;
      var text = '';
      var data = parseFloat(params.value);
      if (params.name.startsWith(this.LABEL_TOKEN)) {
        data= parseFloat(params.name.substring(this.LABEL_TOKEN.length));
        all = true;
      } // if
      switch (type) {
        case 'integer':
          text = data.toLocaleString('en-MX', {style: 'decimal', minimumFractionDigits: 0, maximumFractionDigits: 0}); // 1,234,567
          break;
        case 'double':
          text = data.toLocaleString('es-MX', {style: 'decimal', minimumFractionDigits: 1, maximumFractionDigits: 2}); // 1,234,567.12
          break;
        case 'double-one-decimal':
          text = data.toLocaleString('es-MX', {style: 'decimal', minimumFractionDigits: 1, maximumFractionDigits: 1}); // 1,234,567.1
          break;
        case 'money':
          text = data.toLocaleString('es-MX', {style: 'currency', currency: 'USD', minimumFractionDigits: 1, maximumFractionDigits: 2}); // $1,242.50
          break;
        case 'percent':
          text = this.capital(params.name) + "\n(" + (data / 100).toLocaleString('es-MX', {style: 'percent', minimumFractionDigits: 1, maximumFractionDigits: 2}) + ")"; // (37.53%)
          break;
        case 'keet-double':
          text = this.capital(params.name) + "\n" + (data / 100).toLocaleString('es-MX', {style: 'percent', minimumFractionDigits: 1, maximumFractionDigits: 1}); // 1,234,567.1
          break;
        case 'keet-percent':
          text = this.capital(params.name) + "\n(" + (data / 100).toLocaleString('es-MX', {style: 'percent', minimumFractionDigits: 1, maximumFractionDigits: 1}) + ")"; // 37.5%
          break;
        case 'keet-dona-percent':
          text = this.capital(params.name) + "\n(" + params.percent + "%)";
          break;
        case 'keet-dona-integer':
          text = this.capital(params.name) + "\n" + data.toLocaleString('es-MX', {style: 'decimal', minimumFractionDigits: 0, maximumFractionDigits: 0}); // 1,234,567
          break;
        case 'keet-dona-only-label':
          text = this.capital(params.name);
        break;
        case 'keet-dona-integer-label-free':
          text = params.name + "\n" + data.toLocaleString('es-MX', {style: 'decimal', minimumFractionDigits: 0, maximumFractionDigits: 0}); // 1,234,567
          break;
        case 'keet-dona-label-upper-case':
          text = params.name.toUpperCase();
          break;
        default:
          text = data.toLocaleString('es-MX'); // 1,234,567.123
      } // switch
      return all ? text: '';
    },
    tooltip: function (params, format) {
      var msg = '<div style="text-align: left;">';
      var label = '';
      if (typeof (format) === 'undefined')
        format = 'double';
      $.each(params, function (index, items) {
        if (index === 0)
          label = items['name'];
        if (items['value'] !== '-' && items['value'] !== 0) {
          if (items['seriesName'] === null) {
            msg = msg + items['marker'] + ' ' + $echarts.format(items, format) + '<br/>';
          } else {
            msg = msg + items['marker'] + '  ' + $echarts.legend(items['seriesName']) + ': ' + $echarts.format(items, format) + '<br/>';
          }
        }
      });
      msg = $echarts.label(label) + '<br/>' + msg + '</div>';
      return msg;
    },
    tooltipSingle: function (params, formatType) {
      var single = [];
      single.push(params);
      return $echarts.tooltip(single, formatType);
    },
    legend: function (params, cuantos) {
      var cantidad = 7;
      if (typeof (params) === 'string') {
        if (typeof (cuantos) === 'undefined') {
          cantidad = 7;
        } else {
          cantidad = cuantos;
        }
        if (params.length > cantidad) {
          params = params.toLowerCase();
          params = params.charAt(0).toUpperCase() + params.slice(1);
        } // if	
        if (params.length > 75)
          params = params.substring(0, 75) + '...';
      } // if
      return params;
    },
    responsive: function () {
      $.each(this.charts, function (id) {
        if (window[id])
          window[id].resize();
      });
    },
    reset: function () {
      Object.assign(this.charts, this.backup);
      $.each(this.charts, function (id, value) {
        if (id === $echarts.RESERVED_ID)
          $echarts.search(value);
        else
          $echarts.update(id, value, false);
      });
      this.toggle('display:none;');
      this.selected = {group: $echarts.RESERVED_KEY};
      return false;
    },
    add: function (items, group) {
      $.each(items, function (id, value) {
        $echarts.reserved(id, value);
        if (id === $echarts.RESERVED_ID)
          $echarts.search(value);
        else {
          if ($echarts.charts.hasOwnProperty(id))
            $echarts.update(id, value);
          else {
            $echarts.charts[id] = value;
            $echarts.create(id, value);
          } // else	
          window[id].resize();
        } // if	
      });
      if (this.selected.hasOwnProperty('claveEntidad') && this.carousel.items>0) {
        $echarts.title(this.carousel.items[this.carousel.index], this.history[group][this.carousel.items[this.carousel.index]]);
        $echarts.subTitle(this.carousel.items[this.carousel.index], this.history[group][this.carousel.items[this.carousel.index]]);
      }
    },
    remove: function (id) {
      if ($echarts.charts.hasOwnProperty(id))
        delete $echarts.charts[id];
    },
    exists: function (group, update) {
      if (typeof (update) === 'undefined')
        update = true;
      var ok = this.history[group] !== undefined;
      if (ok && update)
        this.refresh(this.history[group], false);
      return ok;
    },
    valid: function (group) {
      if (typeof (group) === 'undefined')
        if (this.selected.hasOwnProperty('claveEntidad'))
          group = this.selected.group + this.selected.claveEntidad;
        else
          group = this.selected.group;
      var ok = !this.exists(group, true);
      if (ok)
        janal.bloquear();
      else
        this.toggle('');
      return ok;
    },
    clean: function () {
      this.history = {};
    },
    get: function (id) {
      var json = undefined;
      if (this.charts.hasOwnProperty(id))
        json = this.charts[id];
      return json;
    },
    group: function (id) {
      var json = undefined;
      if (this.history.hasOwnProperty(id))
        json = this.history[id];
      return json;
    },
    item: function (params) {
      var token = params.split("|");
      var value = {
        idEntidad: token[0],
        claveEntidad: token[1],
        unidadEjecutora: token[2],
        ambito: token[3],
        descripcion: token[4],
        siglasEntidad: (token.length>5?token[5]:''),                             
        evento: (token.length >= 5 ? token[5] : '')
      };
      Object.assign(this.selected, value);
      return JSON.stringify(value);
    },
    map: function (params) {
      if (typeof loadItemEventMap !== "undefined") {
        loadItemEventMap(this.item(params));
      } else
        console.info('No existe la funcion de javascript llamada [loadItemEventMap]');
    },
    toggle: function (style) {
      if ($(this.nacional).length > 0)
        $(this.nacional).attr('style', style + ' cursor:pointer; padding:4px 10px 4px 10px!important;');
      if ($(this.georreferencia).length > 0)
        $(this.georreferencia).attr('style', style + ' cursor:pointer; padding:4px 10px 4px 10px!important;');
    },
    paint: function (id, group, update) {
      var ok = false;
      if (typeof (group) === 'undefined')
        group = this.selected.group;
      else
      if (typeof (group) === "boolean") {
        update = group;
        group = this.selected.group;
      } // if	
      if (typeof (update) === 'undefined')
        update = false;
      // esto es para actualizar una sola grafica porque sus datos cambiaron
      if (this.history.hasOwnProperty(group)) {
        if (this.history[group].hasOwnProperty(id)) {
          if (update)
            if (id === this.RESERVED_ID)
              this.search(this.history[group][id]);
            else
              this.update(id, this.history[group][id], false);
          ok = true;
        } // if
      } // if
      return ok;
    },
    total: function () {

    },
    selectItem: function (group, params) {
      var ok = !this.valid(group);
      if (ok)
        janal.desbloquear();
      else
        this.map(params);
    },
    toContextImage: function (image) {
      return window.location.origin + janal.toContext() + image;
    }
  });
  console.info('Janal.Control.Echarts initialized');
})(window);
jsEcharts = new Janal.Control.Echarts.Core(Janal.Control.Echarts.names, Janal.Control.Echarts.group);

$(document).ready(function () {
  jsEcharts.load(Janal.Control.Echarts.names);
  jsEcharts.start();
});