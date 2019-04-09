#include <unordered_map>
namespace osm {
namespace query{
enum class Output : unsigned {JSON = 0u, OSM_NATIVE, GEO_JSON};
enum class Type : unsigned {ROUTE = 0u};
enum class Route : unsigned {BUS = 0u};
enum class WriteToFile : bool { NO = false, YES = true};
static std::unordered_map<Output, std::string> output_to_string{{Output::JSON, std::string("json")},
                                                                {Output::OSM_NATIVE, std::string("xml")},
                                                                {Output::GEO_JSON, std::string("geojson")}};
static std::unordered_map<Type, std::string> type_to_string{{Type::ROUTE, "route"}};
static std::unordered_map<Route, std::string> route_to_string{{Route::BUS, "bus"}};

}
}