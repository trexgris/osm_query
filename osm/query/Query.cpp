#include "Query.hpp"
#include "writer/Writer.hpp"
#include <curlpp/cURLpp.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Options.hpp>
#include <curlpp/Exception.hpp>
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cerrno>


namespace osm {
namespace query {
struct Query::Impl final {
    static constexpr const char * const OUTPUT_FILE = "osm_response";
    static constexpr const char * const CMD_GEOJSON_CONVERT = "osmtogeojson";
    Impl(std::string area, const Type type, const Route route, const Output out);
    virtual ~Impl();
    void Send(const WriteToFile write_to_file);
private:
    void SystemOsmToGeoJson();
    Impl() = delete;
    Impl(const Impl&) = delete;
    Impl(Impl&&) = delete;
    Impl& operator=(const Impl&) = delete;
    Impl& operator=(Impl&&) = delete;
private:
    std::string area_;
    Output output_;
    Type route_;
    Route transport_;
};

std::shared_ptr<Query> Query::Make(std::string area, const osm::query::Type type, const osm::query::Route route, const osm::query::Output out) {
    return std::make_shared<Query>(std::move(area), type, route, out);
}

Query::Impl::Impl(std::string area, const Type type, const Route route, const Output out) : area_(std::move(area)), route_(type), transport_(route), output_(out){}

Query::Impl::~Impl() {}

Query::Query(std::string area, const osm::query::Type type, const osm::query::Route route, const osm::query::Output out)
: pimpl_(new Impl(std::move(area), type, route, out)) {}

Query::~Query() {}

void Query::Send(const WriteToFile write_to_file) {
  if (!pimpl_) throw std::logic_error("Pimpl is null");
  pimpl_->Send(write_to_file);  
}

void Query::Impl::SystemOsmToGeoJson() {
    // terrible convert process
    // convert to osm
    const std::string osm_format = std::string("osm");
    std::string res = std::string(OUTPUT_FILE) + std::string(".");
    std::string cmd1 = std::string ("mv ") + res + output_to_string[Output::OSM_NATIVE] + std::string(" ") + res + osm_format;
    std::cout << cmd1 << std::endl;
    if(system(cmd1.c_str())) {
        throw std::runtime_error("SystemOsmToGeoJson Throw");
        return;
    }
    std::string cmd2 = std::string("osmtogeojson ") + res + osm_format + std::string(" > ") +  std::string("t") + res + output_to_string[Output::GEO_JSON];           
    std::cout << cmd2 << std::endl;

    if(system(cmd2.c_str())) {
        throw std::runtime_error("SystemOsmToGeoJson Throw");
        return;
    }
}

void Query::Impl::Send(const WriteToFile write_to_file) {
    curlpp::Cleanup cleaner;
    curlpp::Easy request;

    std::string url = std::string(INTERPRETER_OVERPASS_DATA_URL);

    // overpass QL
    if(!type_to_string.count(route_) || !route_to_string.count(transport_))
        throw std::logic_error("Entry type not found");

    Output output_format = output_;
    if(output_ == Output::GEO_JSON) {
        output_format = Output::OSM_NATIVE;
    }
                
    std::string common = "[type="+type_to_string[route_]+"][route="+route_to_string[transport_]+"]";
    url += "[out:" + output_to_string[output_format] + "];"; 
    url += "area[name="+area_+"]-%3E.boundaryarea;";
    url += "(node"+common;
    url += "(area.boundaryarea);way"+common;
    url += "(area.boundaryarea);%3E;relation"+common+"(area.boundaryarea);%3E%3E;);out%20meta;";

    if(static_cast<bool>(write_to_file)) {
        curlpp::options::WriteFunctionCurlFunction callback(writer::Callback); // c style, can improve
        FILE *file = stdout;
        std::string tmpstr = std::string(OUTPUT_FILE) + std::string(".") + output_to_string[output_format];
        if(tmpstr.c_str()  != nullptr) {
            file = fopen(tmpstr.c_str(), "wb");
            if(file == nullptr) {
                throw std::runtime_error("File is null");
            }
        }
        curlpp::OptionTrait<void *, CURLOPT_WRITEDATA> data(file);        
        request.setOpt(callback);
        request.setOpt(data);
    }
	request.setOpt(new curlpp::options::Url(url));
	request.setOpt(new curlpp::options::Verbose(true));
	request.perform();

    // barrier // tmp hack ?
    if(output_ == Output::GEO_JSON) {
         SystemOsmToGeoJson();
    }
}

}
}