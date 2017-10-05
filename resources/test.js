interface Infrastructure { 
	locatedIn: GeographicalCoordinate! 
	nearByInfrastructure: [Infrastructure] 
	InfrastructureType: String! 
}  
 
type GeographicalCoordinate { 
	latitude: Float! 
	longitude: Float! 
}  
 
type District { 
	districtNumber: Int! 
	districtName: String! 
}  
 
type MetroAndBusStop implements Infrastructure{ 
	locatedIn: GeographicalCoordinate! 
	nearByInfrastructure: [Infrastructure] 
	InfrastructureType: String! 
	stopAddress: String 
	stopPhone: Int 
	stopName: String 
}  
 
type BicingStation implements Infrastructure{ 
	locatedIn: GeographicalCoordinate! 
	nearByInfrastructure: [Infrastructure] 
	InfrastructureType: String! 
	stationStreetName: String! 
	stationType: String! 
	stationBikesNumber: Int! 
	stationID: ID! 
	stationAltitude: Float! 
	stationSlotsNumber: [Int]! 
	stationStreetNumber: Int! 
	nearByStation: [BicingStation] 
	stationStatus: String! 
}  
 
type Suburb { 
	suburbName: String! 
	belongsTo: District! 
	providesStop: [MetroAndBusStop] 
}  
 
