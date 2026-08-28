#ifndef FX_CORE_H
#define FX_CORE_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FxCore FxCore;

FxCore* fx_core_init(void);
void fx_core_deinit(FxCore* core);
const char* fx_core_version(void);
int32_t fx_core_run_agent(FxCore* core, const char* prompt);

#ifdef __cplusplus
}
#endif

#endif /* FX_CORE_H */
