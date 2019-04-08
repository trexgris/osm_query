#include "Query.hpp"

int main() {
    auto ptr = std::make_shared<osm::query::Query>("Nicaragua",osm::query::Type::ROUTE, osm::query::Route::BUS,osm::query::Output::OSM_NATIVE);
    ptr->Send(osm::query::WriteToFile::YES);
    return 0;
}
