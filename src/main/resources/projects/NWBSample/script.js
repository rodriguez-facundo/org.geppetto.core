Instances.getInstance("nwbSample.metadata");
Instances.getInstance("nwbSample.time");

$(".simulation-controls").children().attr('disabled', 'disabled')
$("#genericHelpBtn").removeAttr('disabled')

G.addWidget(1).setData(nwbSample.metadata).setName('Metadata').setPosition(713,228).setSize(525.8,580.8);
G.addWidget(1).setMessage(Project.getActiveExperiment().getDescription()).setSize(150,581.8).setPosition(713,68.5).setName("Description");

GEPPETTO.ControlPanel.setColumnMeta([{ "columnName": "path", "order": 1, "locked": false, "displayName": "Path", "source": "$entity$.getPath()" }, { "columnName": "sweep", "order": 2, "locked": false, "displayName": "Sweep No.", "source": "$entity$.getName()" }, { "columnName": "name", "order": 3, "locked": false, "displayName": "Name", "source": "$entity$.getVariables()[1].getType().getVariables()[0].getValue().getWrappedObj()['value']['text']" }, { "columnName": "amplitude", "order": 4, "locked": false, "displayName": "Amplitude", "source": "$entity$.getVariables()[1].getType().getVariables()[1].getValue().getWrappedObj()['value']['text']" }, { "columnName": "interval", "order": 5, "locked": false, "displayName": "Interval", "source": "$entity$.getVariables()[1].getType().getVariables()[2].getValue().getWrappedObj()['value']['text']" }, { "columnName": "controls", "order": 6, "locked": false, "customComponent": GEPPETTO.ControlsComponent, "displayName": "Controls", "source": "", "action": "GEPPETTO.FE.refresh();" }]);
GEPPETTO.ControlPanel.setColumns(['sweep', 'name', 'amplitude', 'interval','controls']);
GEPPETTO.ControlPanel.setDataFilter(function(entities) { var compositeInstances = GEPPETTO.ModelFactory.getAllTypesOfMetaType(GEPPETTO.Resources.COMPOSITE_TYPE_NODE, entities); var sweepInstances = []; for (index in compositeInstances) { allVariables = compositeInstances[index].getVariables(); for(v in allVariables){ if(allVariables[v].getId().startsWith('Sweep_')){ s_r = allVariables[v].getType().getVariables(); sweepInstances.push(s_r[0].getType()); sweepInstances.push(s_r[1].getType()); } } } return sweepInstances; }); 
GEPPETTO.ControlPanel.setControlsConfig({ "VisualCapability": {}, "Common": { "plot": { "id": "plot", "actions": ["$instance$.getVariables()[0].getValue().resolve(function(){ var instancePath = ''; for (var i = 0; i < GEPPETTO.ModelFactory.allPaths.length; i++) { if (GEPPETTO.ModelFactory.allPaths[i].type == $instance$.getPath()) { instancePath = GEPPETTO.ModelFactory.allPaths[i].path + '.recording'; break; } } G.addWidget(0).plotXYData(Instances.getInstance(instancePath), nwbSample.time).setSize(273.8,556.8).setPosition(130,35).setName(instancePath); }); "], "icon": "fa-area-chart", "label": "Plot", "tooltip": "Plot Sweep" } } });
GEPPETTO.ControlPanel.setControls({ "VisualCapability": [], "Common": ['plot'] });

GEPPETTO.ControlPanel.addData(Instances);
GEPPETTO.ControlPanel.open();