Instances.getInstance("nwbSample.stimulus")
Instances.getInstance("nwbSample.response")
Instances.getInstance("nwbSample.time")
G.addWidget(0).plotXYData(nwbSample.time,nwbSample.stimulus).plotXYData(nwbSample.time,nwbSample.response)
G.addWidget(0).plotXYData(nwbSample.time,nwbSample.response)
Plot2.setSize(390.8,564.8).setPosition(514,138).setName("Response")
Plot1.setPosition(140,82).setName("Stimulus and Response")