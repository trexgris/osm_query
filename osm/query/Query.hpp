#pragma once
#include <memory>
#include <string>
#include "Types.hpp"
namespace osm {
namespace query {
class Query final {
public:
  static constexpr const char * const INTERPRETER_OVERPASS_DATA_URL = "https://www.overpass-api.de/api/interpreter?data=";  
  static std::shared_ptr<Query> Make(std::string area, const osm::query::Type type, const osm::query::Route route, const osm::query::Output out);
  Query(std::string area, const osm::query::Type type, const osm::query::Route route, const osm::query::Output out);
  ~Query();
  void Send(const WriteToFile write_to_file = osm::query::WriteToFile::NO);
private:
  Query() = delete;
  Query(const Query&) = delete;
  Query(Query&&) = delete;
  Query& operator=(const Query&) = delete;
  Query& operator=(Query&&) = delete;

private: 
    struct Impl;
    std::unique_ptr<Impl> pimpl_;        
};    
}
}