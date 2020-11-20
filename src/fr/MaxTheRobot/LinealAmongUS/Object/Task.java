package fr.MaxTheRobot.LinealAmongUS.Object;

public enum Task {
	FIX_WIRING("FixWire"), SCAN("MedScan"), CARD("SwipeCard"), SCAN_PASS("ScanBoardingPass"), DATA("DownloadData"), RECORD_TEMP("RecordTemperature"), DIVERT_POWER("DivertPower"), ENGINE_OUTPUT("FixEngineOutput"), TELESCOPE("AlignTelescope"), ASSEMBLE_ARTIFACT("AssembleArtifact"), BUY_BEVERAGE("BuyBeverage"), CALIBRATE_DISTRIBUTOR("CalibrateDistributor"), COURSE("ChartCourse"), O2("CleanO2Filter"), EMPTY_GARBAGE("EmptyGarbage"), CANISTERS("FillCanisters"), WEATHER("FixWeatherNode"), TREE("MonitorTree"), SHIELDS("PrimeShields"), REPAIR_DRILL("RepairDrill"), STEERING("StabiliseSteering"), STORE_ARTIFACTS("StoreArtifacts"), MANIFOLD("UnlocManifolds"), START_REACTOR("StartReactor");
	public final String getName;
	
	private Task(String getName) {
		this.getName = getName;
	}
}

